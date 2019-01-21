package com.jhcomn.lambda.mllib.hfct1500

import java.io.{FileInputStream, ObjectInputStream}
import java.util

import breeze.linalg.{DenseMatrix => BDM}
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.jhcomn.lambda.mllib.base.adapter.HfctTagAdapter
import com.jhcomn.lambda.mllib.base.AbstractMLController
import com.jhcomn.lambda.mllib.base.callback.{PreprocessCallback, TrainingCallback, ProcessCallback}
import com.jhcomn.lambda.mllib.base.serializer.{BaseSerializer, ISerializer}
import com.jhcomn.lambda.mllib.hfct1500.Hfct1500PrpdReader
import com.jhcomn.lambda.mllib.hfct1500.classify.{HFCTClassifyMetaMessage, HFCT_classify}
import com.jhcomn.lambda.mllib.hfct1500.preprocess.HFCT1500PreprocessUtils
import com.jhcomn.lambda.mllib.hfct1500.training.NN.{NeuralNetModelWithTags, NeuralNetModel, NeuralNet}
import com.jhcomn.lambda.packages.IPackage
import com.jhcomn.lambda.packages.tag.hfct1500.HfctTagWithResult
import org.apache.hadoop.fs.{FileSystem, FSDataInputStream}
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer
import com.jhcomn.lambda.packages.tag.Tag

/**
  * Created by shi mn on 2017/5/16.
  */
/**
  * 训练样本 HFCTFeature
  */
@SerialVersionUID(1L) case class HFCTFeature(@BeanProperty val tag: String,
                       @BeanProperty val features: Array[Double]) extends IPackage

@SerialVersionUID(1L) case class HFCTResult(@BeanProperty val tags: util.List[HfctTagWithResult]) extends IPackage

class HFCT1500Controller(@transient private val spark: SparkSession,
                         @transient private val fileSystem: FileSystem) extends AbstractMLController(spark, fileSystem) with Serializable {

  {
    if (spark == null)
      throw new RuntimeException("spark不能为null！")
  }

  //获取spark上下文sparkcontext
  @transient
  private val sc = spark.sparkContext
  @transient
  private val serializer: ISerializer = new BaseSerializer(fileSystem)
  @transient
  private val modelFileName: String = "nn.model"
  @transient
  private val adapter: HfctTagAdapter = new HfctTagAdapter()

  /**
    * step1:数据预处理
    *
    * @param inputs      每个文件的数据流  --> 存在频繁开闭读取流的操作，消耗IO，耗时
    * @param tag         该文件的标签，若无标签则为null
    * @param parquetPath 预处理完毕结构化数据存储的表路径
    * @param callback    数据预处理回调
    */
  override def preprocessWithStream(inputs: FSDataInputStream, tag: String, parquetPath: String, callback: PreprocessCallback): Unit = ???

  /**
    * step1:数据预处理
    *
    * @param isTraining  是否为训练集，若是返回null
    * @param srcPath     每个文件的绝对路径  --> 可直接调用sparkContext.textFile()读取
    * @param tagId         该文件的标签id，若无标签则为null
    * @param parquetPath 预处理完毕结构化数据存储的表路径
    * @param callback    数据预处理回调
    * @return list[pkg] 返回结构化数据列表
    */
  override def preprocessWithUrl(isTraining: Boolean,
                                 srcPath: String,
                                 tagId: String,
                                 parquetPath: String,
                                 callback: PreprocessCallback): util.List[IPackage] = {

    if (callback != null)
      callback.onStart("开始HFCT训练数据预处理")
    //校验
    if (srcPath == null || srcPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("srcPath or parquetPath is null")
      if (callback != null)
        callback.onError("srcPath or parquetPath is null")
      return null
    }

    //读取原始数据文件转为dataframe，即结构化的rdd
//    import spark.implicits._
//    val results: java.util.List[IPackage] = new java.util.ArrayList()
    if (isTraining) {
      //训练样本提取
      val K = HFCT1500PreprocessUtils.preprocessBeforeTraining(sc, srcPath, fileSystem)
      if (K != null) {
        val featureModel = new HFCTFeature(tagId, K)
        val models = List(featureModel)
        try {
          //featureModel写入parquet
          val df = spark.createDataFrame(models)
          df.write.mode(SaveMode.Append).parquet(parquetPath)
        } catch {
          case ex: Exception => println(ex)
        }
      }
      else {
        println("无效数据")
      }
    }

    if (callback != null) {
      callback.onStop("结束HFCT训练数据预处理") //结束预处理
//      spark.read.parquet(parquetPath).show()
    }

    return null
  }

  /**
    * step2:模型训练
    *
    * @param parquetPath     训练数据存储的表路径
    * @param modelPath       模型存储路径
    * @param modelBackupPath 备份模型存储路径
    * @param callback        模型训练回调
    */
  override def train(parquetPath: String, modelPath: String, modelBackupPath: String, callback: TrainingCallback): Unit = {
    if (callback != null)
      callback.onStart("开始HFCT模型训练") //开始训练模型
    //校验
    if (modelPath == null || modelPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("modelPath or parquetPath is null")
      if (callback != null)
        callback.onError("modelPath or parquetPath is null")
      return
    }
    //训练
    //step1:更新hfct tag数组
    if (adapter != null)
      adapter.update()

    try {
      println("开始HFCT数据格式转换")
      @transient val tags = adapter.getTags.clone()
      println("parquetPath = " + parquetPath)
      val rawData = spark.read.parquet(parquetPath)
      //Dataframe to RDD[HFCTFeature] to Array[HFCTFeature]
      val data = rawData.rdd.map(row => {
        val tagId = row.getAs[String]("tag") //tagId
        val features = row.getAs[Seq[Double]]("features") //此处必须先转为Seq，再转Array reference：http://blog.csdn.net/lsshlsw/article/details/49095663
        //TODO tagId --> tag
        val tag = HFCT1500Controller.getTagById(tags, tagId)
        new HFCTFeature(tag, features.toArray)
      })
//      println("DataFrame 转 HFCTFeature model, size = " + data.count())
//      data.foreach(model => {
//        println(model.tag)
//        println(model.features.length)
//      })
      //转成训练数据，并缓存
      val trainingData = data.map { rdd =>
        println(rdd.toString)
        val tag = rdd.getTag
        val features = rdd.getFeatures
        val arrChar = tag.toCharArray
        val tagArr = new ArrayBuffer[Double]()
        for (i <- 0 until arrChar.length)
          tagArr += (arrChar(i) - 48)
        (new BDM(1, tagArr.length, tagArr.toArray), new BDM(1, features.length, features))
      }.cache()

      println("开始HFCT模型训练")
      val opts = Array(10.0, 100.0, 0.0) //第二个元素1000是迭代次数
      //设置训练参数，建立模型
      val outputNums = adapter.getTags.length
      println("output节点数：" + outputNums)
      val NNmodel = new NeuralNet().
          setSize(Array(7200, 10, outputNums)). //TODO 'adapter.getTags.length'为输出节点，应为变量 = 所有类别总数（不包括无效数据&噪声两类）
          setLayer(3).
          setActivation_function("sigm").
          setLearningRate(2.0).
          setScaling_learningRate(1.0).
          setWeightPenaltyL2(0.0).
          setNonSparsityPenalty(0.0).
          setSparsityTarget(0.0).
          setOutput_function("sigm").
          NNtrain(trainingData, opts)

      if (callback != null) {
        callback.onOverwriteModel(modelPath, modelBackupPath)
      }
      //释放训练数据缓存
      trainingData.unpersist()
      println("结束HFCT模型训练，保存模型")
      //序列化模型，持久化到HDFS
      val NNmodelWithTags = new NeuralNetModelWithTags(adapter.getTags, NNmodel)
      serializer.serialize(NNmodelWithTags, modelPath + "/" + modelFileName)
      serializer.serialize(NNmodelWithTags, modelBackupPath + "/" + modelFileName)
      //更新模型json到数据库同步
      val json = JSON.toJSONString(NNmodelWithTags, SerializerFeature.PrettyFormat)
      if (callback != null) {
        callback.onStop(json)
      }
      //Test:反序列化模型
//      val nnModel = serializer.deserialize[NeuralNetModel](modelBackupPath + "/" + modelFileName)
//      println("NNConfig = " + nnModel.getConfig + ", NNModel weights = " + nnModel.getWeights)
    } catch {
      case ex:Exception => {
        println(ex)
        //callback.onError("数据格式转换or训练模型出错：" + ex.toString)
      }
    } finally {

    }
  }

  /**
    * step3:数据分类/识别
    *
    * @param modelPath 模型存放路径
    * @param data      测试数据
    * @param callback  数据分类/识别回调
    * @return 返回结果
    */
  override def analyze(modelPath: String, data: IPackage, callback: ProcessCallback): IPackage = {
    if (callback != null)
      callback.onStart("start process HFCT") //开始测试
    //校验
    if (modelPath == null || modelPath.equals("")) {
      println("modelPath is null")
      if (callback != null)
        callback.onError("modelPath is null")
      return null
    }
    //加载模型 --> 反序列化
    val nnModelWithTag = serializer.deserialize[NeuralNetModelWithTags](modelPath + "/" + modelFileName)
    val nnModel = nnModelWithTag.getModel
    //此时以未更新模型为准做识别
    val tags = nnModelWithTag.getTags
    if (adapter != null)
      adapter.update(tags)

    if (nnModel == null) {
      callback.onError("HFCT Model加载为空")
      return null
    }
    //HFCT分类
    //    val rddData = sc.textFile(data.asInstanceOf[HFCTClassifyMetaMessage].dataPath)
    //    val result = HFCT_classify.classify(rddData, nnModel, tags.length).rate
    try {
      //读取分析文件
      val reader = new Hfct1500PrpdReader()
      val testingData = reader.readHDFS(data.asInstanceOf[HFCTClassifyMetaMessage].dataPath, fileSystem)
      //分析分类
      val result = HFCT_classify.classify(testingData, nnModel, tags.length).rate
      //打包分析结果
      if (result != null && result.length == tags.length + 2) {
        //json list存储结果
        val list = new util.ArrayList[HfctTagWithResult]()

        if (result(0).asInstanceOf[Int] == 1) {
          list.clear()
          list.add(new HfctTagWithResult(new Tag(), "无效数据"))
        }
        else if (result(1).asInstanceOf[Int] == 1) {
          list.clear()
          list.add(new HfctTagWithResult(new Tag(), "噪声"))
        }
        else {
          list.clear()
          for (i <- 2 until result.length) {
            list.add(new HfctTagWithResult(adapter.getTagByIndex(i - 2), ("" + result(i))))
          }
        }
        //return json 结果包
        return new HFCTResult(list)
      }
    } catch {
      case ex: Exception => println(ex)
    }
    if (callback != null)
      callback.onStop("stop process HFCT") //结束测试

    null
  }
}

object HFCT1500Controller {
  def main(args: Array[String]) {
  }
  def getTagById(tags: Array[Tag], id: String) : String = {
    for (i <- 0 until tags.length) {
      val tag = tags(i);
      if (tag.getTagId.equalsIgnoreCase(id)) {
        println(tag.getTag)
        return tag.getTag
      }
    }
    null
  }
}

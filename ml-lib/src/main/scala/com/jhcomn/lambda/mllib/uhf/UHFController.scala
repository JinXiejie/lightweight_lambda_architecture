package com.jhcomn.lambda.mllib.uhf

import java.util

import com.jhcomn.lambda.mllib.base.AbstractMLController
import com.jhcomn.lambda.mllib.base.adapter.{UhfTagAdapter, UwTagAdapter}
import com.jhcomn.lambda.mllib.base.callback.{PreprocessCallback, ProcessCallback, TrainingCallback}
import com.jhcomn.lambda.mllib.base.serializer.{BaseSerializer, ISerializer}
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFPreprocessUtil
import com.jhcomn.lambda.mllib.uhf.{UHFController, UhfFeature, UhfResult, UhfXGBModelWithTags}
import com.jhcomn.lambda.mllib.uw1000.preprocess.UW1000PreprocessUtil
import com.jhcomn.lambda.packages.IPackage
import com.jhcomn.lambda.packages.tag.Tag
import com.jhcomn.lambda.packages.tag.UHF.UhfTagWithResult
import ml.dmlc.xgboost4j.scala.spark.XGBoost
import ml.dmlc.xgboost4j.scala.{Booster, DMatrix}
import org.apache.hadoop.fs.{FSDataInputStream, FileSystem}
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.beans.BeanProperty

/**
  * 超声波局部放电检测
  * Created by jinxj on 2019/01/04.
  */
/**
  * 特高频法GIS局放识别结构化特征数据
  *
  * @param tagId
  * @param features
  */
@SerialVersionUID(1L) case class UhfFeature(@BeanProperty val tagId: String,
                                               @BeanProperty val features: String) extends IPackage

/**
  * 超声波XGB局放模型带标签属性
  *
  * @param tags
  * @param model
  * Created by shimn on 2018/1/22.
  */
@SerialVersionUID(1L) case class UhfXGBModelWithTags(@BeanProperty val tags: Array[Tag],
                                                        @BeanProperty val model: Booster) extends IPackage

@SerialVersionUID(1L) case class UhfResult(@BeanProperty val tags: util.List[UhfTagWithResult]) extends IPackage

@SerialVersionUID(1L) case class UhfTransTag(@BeanProperty val ydTag: String,
                                                @BeanProperty val xgbTag: String) extends IPackage


class UHFController(@transient private val spark: SparkSession,
                    @transient private val fileSystem: FileSystem) extends AbstractMLController(spark, fileSystem) with Serializable  {

  //初始化
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
  private val modelFileName: String = "uhf.model"
  @transient
  private val modelWithTagsFileName: String = "uhf_tags.model"
  @transient
  private val adapter: UhfTagAdapter = new UhfTagAdapter()

  /**
    * step1:数据预处理
    *
    * @param inputs      每个文件的数据流  --> 存在频繁开闭读取流的操作，消耗IO，耗时
    * @param tag         该文件的标签，若无标签则为null，有标签的分两种：1(正常)和999(其他缺陷)
    * @param parquetPath 预处理完毕结构化数据存储的表路径
    * @param callback    数据预处理回调
    */
  override def preprocessWithStream(inputs: FSDataInputStream, tag: String, parquetPath: String, callback: PreprocessCallback): Unit = ???

  /**
    * step1:数据预处理
    *
    * @param isTraining  弃用，默认送入"true"
    * @param srcPath     每个文件的绝对路径  --> 可直接调用sparkContext.textFile()读取
    * @param tagId         该文件的标签，若无标签则为null
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
      callback.onStart("开始UHF局放数据预处理")
    //校验
    if (srcPath == null || srcPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("srcPath or parquetPath is null")
      if (callback != null)
        callback.onError("srcPath or parquetPath is null")
      return null
    }

    val ret: util.List[IPackage] = new util.ArrayList[IPackage]()
    //读取原始数据文件转为dataframe，即结构化的rdd
    if (isTraining) {
      //样本特征提取
      //实例化预处理工具
      val util = new UHFPreprocessUtil(sc, fileSystem)
      //特征提取
      val features = util.generateFeatures(srcPath, tagId)
      //将特征数据包装成parquet存储支持的dataframe格式
      if (features != null) {
        val featureModel = new UhfFeature(util.label, features)
        val models = List(featureModel)
        ret.add(featureModel)
        try {
          //featureModel写入parquet
          val df = spark.createDataFrame(models)
          //以追加模式持久化新增特征数据
          df.write.mode(SaveMode.Append).parquet(parquetPath)
        } catch {
          case ex: Exception => println(ex)
        }
      }
      else {
        println("无效数据")
      }

      if (callback != null) {
        callback.onStop("结束UHF数据预处理") //结束预处理
      }
    }

    ret
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
      callback.onStart("开始UHF模型训练") //开始训练模型
    //校验
    if (modelPath == null || modelPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("modelPath or parquetPath is null")
      if (callback != null)
        callback.onError("modelPath or parquetPath is null")
      return
    }
    //训练
    //step1:更新UHF tag数组
    if (adapter != null)
      adapter.update()

    try {
      import spark.implicits._
      println("开始UHF数据格式转换")
      //获取超声波标签数组
      @transient val tags = adapter.getTags.clone()
      //读取parquet数据
      val rawData = spark.read.parquet(parquetPath)
      //将从parquet读取的dataframe数据转为spark rdd形式 --> 这一步看模型训练需要的数据格式
      val trainData = rawData.map(row => {
        val tagId = row.getAs[String]("tagId")
        val label = UHFController.getTagById(tags, tagId).toDouble
        val feature = row.getAs[String]("features").split(";").map(x => x.toDouble)
        LabeledPoint(label, Vectors.dense(feature))
      }).rdd.cache()

      //      println("label = " + trainData.take(1).apply(0).label)

      println("开始UHF模型训练")
      //迭代次数通CV确定
      val numIter = 80
      val paramMap = List(
        "booster" -> "gbtree",
        "eval_metric" -> "error",
        "seed" -> 1024,
        "eta" -> 0.05f,
        "max_depth" -> 6,
        "min_child_weight" -> 1,
        "lambda" -> 0.65f,
        "silent" -> 1,
        "objective" -> "binary:logistic",
        "nthread" -> 1
      ).toMap

      println(paramMap)

      val model = XGBoost.trainWithRDD(trainData, paramMap, numIter, 1, null, null, false, Float.NaN)
      println("xgb model ok")
      if (callback != null) {
        callback.onOverwriteModel(modelPath, modelBackupPath)
      }
      //释放训练数据缓存
      trainData.unpersist()
      println("结束UHF模型训练，保存模型")
      //序列化模型，持久化到HDFS
      //持久化同步平板的模型booster --> uw1000.model
      serializer.serialize(model.booster, modelPath + "/" + modelFileName)
      serializer.serialize(model.booster, modelBackupPath + "/" + modelFileName)
      println("save uhf.model ok...")
      //持久化带标签信息的同步平板的模型booster --> uhf_tags.model
      val modelWithTags = new UhfXGBModelWithTags(adapter.getTags, model.booster)
      serializer.serialize(modelWithTags, modelPath + "/" + modelWithTagsFileName)
      serializer.serialize(modelWithTags, modelBackupPath + "/" + modelWithTagsFileName)
      println("save uhf_tags.model ok...")

      //更新uw模型元信息到数据库同步
      if (callback != null) {
        callback.onStop(modelBackupPath + "/" + modelWithTagsFileName);
      }
    } catch {
      case ex: Exception => {
        println(ex)
        callback.onError("UHF模型训练出错：" + ex.toString)
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
      callback.onStart("start process UHF")  //开始测试
    //校验
    if (modelPath == null || modelPath.equals("")) {
      println("modelPath is null")
      if (callback != null)
        callback.onError("modelPath is null")
      return null
    }
    //加载模型 --> 反序列化
    println("开始反序列化模型...")
    val modelWithTags = serializer.deserialize[UhfXGBModelWithTags](modelPath + "/" + modelWithTagsFileName)
    val model = modelWithTags.getModel
    //此时以未更新模型为准做识别
    val tags = modelWithTags.getTags
    //    println("tag's len = " + tags.length + "\t model = " + model.toString())
    if (adapter != null)
      adapter.update(tags)

    if (model == null) {
      callback.onError("UHF Model加载为空")
      return null
    }

    val list = new util.ArrayList[UhfTagWithResult]()
    //数据转换
    println("数据转换中......")
    val testData = data.asInstanceOf[UhfFeature]
    //    println("[ " + testData.tag + ", " + testData.features + " ] is processing...")
    val f = testData.features.split(";").map(x => x.toFloat)
    println("数据转换结束，特征维数 = " + f.length)
    val rate = model.predict(new DMatrix(f, 1, f.length))(0)(0)
    println("booster analyze return : " + rate)
    //二分类
    if (rate >= 0.5f)
      list.add(new UhfTagWithResult(adapter.getTagByTag("1"), rate))
    else
      list.add(new UhfTagWithResult(adapter.getTagByTag("0"), rate))

    return new UhfResult(list)
  }
}
object UHFController {
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

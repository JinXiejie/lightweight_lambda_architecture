package com.jhcomn.lambda.mllib.helloworld

import java.io.{BufferedReader, InputStreamReader}

import com.jhcomn.lambda.mllib.base.AbstractMLController
import com.jhcomn.lambda.mllib.base.callback.{PreprocessCallback, ProcessCallback, TrainingCallback}
import com.jhcomn.lambda.packages.IPackage
import org.apache.hadoop.fs.{FileSystem, FSDataInputStream}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
  * helloworld测试
  * 实现：kmeans实现聚类，分析中国足球在亚洲处于什么水平
  * 参考：http://www.jianshu.com/p/32e895a940a2
  * Created by shimn on 2017/4/20.
  */
//first == 2006年世界杯	second == 2010年世界杯	third == 2007年亚洲杯
case class HelloWorld(first: Double, second: Double, third: Double, tag: String) extends IPackage
//结果model，kind为所属子集索引
case class HelloWorldResult(kind: String) extends IPackage

/**
  * 聚类kmeans质心结构体
  *
  * @param id
  * @param point
  */
private case class Cluster(id: Int, point: String) extends IPackage
private object Cluster extends IPackage {
  def apply(r: Row): Cluster = {
    Cluster(r.getInt(0), r.getAs[String](1))
  }
}

class HelloWorldMLController(@transient private val spark: SparkSession,
                             @transient private val fileSystem: FileSystem) extends AbstractMLController(spark, fileSystem) with Serializable {

  {
    if (spark == null)
      throw new RuntimeException("spark不能为null！")
  }

  //获取spark上下文sparkcontext
  @transient
  val sc = spark.sparkContext

  /**
    * step1:数据预处理
    *
    * @param srcPath     每个文件的绝对路径  --> 可直接调用sparkContext.textFile()读取
    * @param tag         该文件的标签，若无标签则为null
    * @param parquetPath 预处理完毕结构化数据存储的表路径
    * @param callback    数据预处理回调
    */
  override def preprocessWithUrl(isTraining: Boolean,
                                 srcPath: String,
                                 tag: String,
                                 parquetPath: String,
                                 callback: PreprocessCallback): java.util.List[IPackage] =
  {
    if (callback != null)
      callback.onStart("start preprocess hello-world demo") //开始预处理

    //校验
    if (srcPath == null || srcPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("srcPath or parquetPath is null")
      if (callback != null)
        callback.onError("srcPath or parquetPath is null")
      return null
    }

    //读取原始数据文件转为dataframe，即结构化的rdd
    import spark.implicits._

    //val results: java.util.List[IPackage] = new java.util.ArrayList() //用于回调结构化数据

    val rdd = sc
      .textFile(srcPath)
      .map(_.split(" "))
      .map(line => {
        HelloWorld(line(0).trim.toDouble, line(1).trim.toDouble, line(2).trim.toDouble, tag)
      })
      .cache()

    val results: java.util.List[IPackage] = new java.util.ArrayList()
    if (!isTraining) {
      //测试数据，返回结构化数据包，不做持久化
      val temp = rdd.collect()
      temp.foreach(value => {
        results.add(value)
      })
    } else {
      //训练数据，不返回结构化数据包，需持久化
      val df = rdd.toDF()
      //写入parquet
      df.write.mode(SaveMode.Append).parquet(parquetPath)

    }

    if (callback != null) {
      rdd.unpersist()
      callback.onStop("stop preprocess hello-world demo") //结束预处理
    }

    results
  }

  /**
    * step1:数据预处理
    *
    * @param inputs        每个文件的数据流
    * @param tag           该文件的标签，若无标签则为null
    * @param parquetPath   预处理完毕结构化数据存储的表路径
    * @param callback      数据预处理回调
    */
  override def preprocessWithStream(inputs: FSDataInputStream,
                                    tag: String,
                                    parquetPath: String,
                                    callback: PreprocessCallback): Unit =
  {
    if (callback != null)
      callback.onStart("start preprocess hello-world demo") //开始预处理

    //获取输入流
    if (inputs == null) {
      System.out.println("FSDataInputStream is null")
      if (callback != null)
        callback.onError("FSDataInputStream is null")
      return
    }
    val reader: BufferedReader = new BufferedReader(new InputStreamReader((inputs)))

    var line: String = null
    while ((line = reader.readLine()) != null) {
      //TODO 数据预处理具体操作
    }

    if (callback != null)
      callback.onStop("stop preprocess hello-world demo") //结束预处理
  }

  /**
    * step2:模型训练
    *
    * @param parquetPath   训练数据存储的表路径
    * @param modelPath     模型存储路径
    * @param modelBackupPath  备份模型存储路径
    * @param callback      模型训练回调
    */
  override def train(parquetPath: String,
                     modelPath: String,
                     modelBackupPath: String,
                     callback: TrainingCallback): Unit =
  {
    if (callback != null)
      callback.onStart("start train hello-world demo") //开始训练模型
    //校验
    if (modelPath == null || modelPath.equals("") || parquetPath == null || parquetPath.equals("")) {
      println("modelPath or parquetPath is null")
      if (callback != null)
        callback.onError("modelPath or parquetPath is null")
      return
    }

    //训练
    val rawData = spark.read.parquet(parquetPath)
//    rawData.show(15) //test
//    rawData.printSchema() //test
    println("parquet file read end, start to train now")
    print("data numbers = " + rawData.count())

    //转换数据格式
    try {
      println("开始数据格式转换")
      val data =
        rawData
          .rdd
          .map(row => Vectors.dense(row(0).asInstanceOf[Double], row(1).asInstanceOf[Double], row(2).asInstanceOf[Double]))
      data.cache()

      println("开始kmeans模型训练")
      //分为3个子集，最多50次迭代
      val kMeansModel = KMeans.train(data, 3, 50)

      //TODO 更新模型训练进度
      if (callback != null)
        callback.onProgress(50f)

      //输出每个子集的质心
      kMeansModel.clusterCenters.foreach(println)

      val kMeansCost = kMeansModel.computeCost(data)
      // 输出本次聚类操作的收敛性，此值越低越好
      println("结束kmeans模型训练，K-Means Cost: " + kMeansCost)
      //保存训练完的模型
      //TODO 保存两份：
      // Exception : org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs:
      // 先删除 TODO test
      if (callback != null) {
        callback.onOverwriteModel(modelPath, modelBackupPath)
      }
      println("结束kmeans模型训练，保存模型")
      kMeansModel.save(sc, modelPath) //一份modelPath
      kMeansModel.save(sc, modelBackupPath) //一份modelBackupPath备份

      //释放缓存
      data.unpersist()

      if (callback != null) {
        //TODO 模型结构体model转json（即字符串String）传回
        callback.onStop(model2json(kMeansModel)) //结束训练模型,返回json串
      }

    } catch {
      case ex: Exception => callback.onError("数据格式转换or训练模型出错：" + ex.toString)
    } finally {
    }

  }

  /**
    * 模型结构体model转json（即字符串String）
    *
    * @param model
    * @return
    */
  def model2json(model: KMeansModel): String = {
    // TODO org.apache.spark.SparkException: Task not serializable
    // refer http://blog.csdn.net/sogerno1/article/details/45935159
    val data = sc.parallelize(model.clusterCenters.zipWithIndex).map { case (point, id) =>
      Cluster(id, point.toJson)
    }
    val array = data.collect()
    val ret = new StringBuilder
    ret ++= "["
    array.map(cluster => {
      ret ++= ("{\"id\":" + cluster.id + "," + cluster.point.substring(1, cluster.point.length-1) + "},")
    })
    return ret.toString().substring(0, ret.length-1) + "]"
  }

  /**
    * step3:数据分类/识别
    *
    * @param modelPath 模型存放路径 --> 此处应采用backup model，保证并发安全
    * @param data      测试数据
    * @param callback  数据分类/识别回调
    * @return 返回结果
    */
  override def analyze(modelPath: String,
                       data: IPackage,
                       callback: ProcessCallback): IPackage =
  {
    if (callback != null)
      callback.onStart("start process hello-world demo") //开始测试
    //校验
    if (modelPath == null || modelPath.equals("")) {
      println("modelPath is null")
      if (callback != null)
        callback.onError("modelPath is null")
      return null
    }

    //加载模型
    val kMeansModel = KMeansModel.load(sc, modelPath)
    if (kMeansModel == null) {
      callback.onError("kMeansModel加载为空")
      return null
    }

    //数据转换
    val raw = data.asInstanceOf[HelloWorld]
    println("[" + raw.first + "," + raw.second + "," + raw.third + "] is processing")
    val vec = Vectors.dense(raw.first, raw.second, raw.third)
    //输出数据及其所属的子集索引
    val result = kMeansModel.predict(vec)
    println(result + ": " + vec)

    if (callback != null)
      callback.onStop("stop process hello-world demo") //结束测试

    return new HelloWorldResult(result + "")
  }

}

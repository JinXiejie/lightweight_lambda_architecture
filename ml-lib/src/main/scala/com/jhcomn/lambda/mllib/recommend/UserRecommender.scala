package com.jhcomn.lambda.mllib.recommend

import java.util
import java.util.{Date, UUID}

import com.jhcomn.lambda.mllib.base.AbstractMLTrainingController
import com.jhcomn.lambda.packages.IPackage
import com.jhcomn.lambda.packages.test.UserMLResultPackage
import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import collection.mutable.HashMap
import scala.util.Random

/**
  * spark操作入口 sparkSession
  * 数据源绝对路径 srcPath
  * 机器学习模型保存绝对路径 modelPath
  * Created by shimn on 2017/1/3.
  */
case class User(id: Long,
                gender: String,
                age: String,
                location: String,
                advantagedSubject: String,
                disAdvantagedSubject: String)

case class UserSimilarity(var id: Long,
                          var similarity: Double)

/**
  * 用于缓存到parquet的model
  *
  * @param id
  * @param recommends
  */
case class UserResult(id: Long,
                      recommends: String)

/**
  *
  * @param spark
  * @param id 任务id，等同于数据包id="uid_date"
  * @param srcPath
  * @param modelPath
  */
class UserRecommender (private val spark: SparkSession,
                       override val id: String,
                       override val srcPath: String,
                       override val modelPath: String,
                       override val cachePath: String)
  extends AbstractMLTrainingController(spark, id, srcPath, modelPath, cachePath) {

  val sc = spark.sparkContext

  override def toString: String = "src path = " + srcPath + " ;\t" + "model path = " + modelPath + " ;\t" + "cache path = " + cachePath

  //开始模型训练并得出结果
  override def start: Unit = {
    import spark.implicits._
    if (spark != null) {
      println("parquet file read begin")
      /*if (srcPath == null) {
        //设置schema结构
        val schema = StructType(
          Seq(
            StructField("id",StringType,true)
            ,StructField("gender",StringType,true)
            ,StructField("age",StringType,true)
            ,StructField("location",StringType,true)
            ,StructField("advantagedSubject",StringType,true)
            ,StructField("disAdvantagedSubject",StringType,true)
          )
        )
        val rawData = sc.textFile("file:\\D:\\jhcomnBigData\\lightweight_lambda_architecture\\ml-lib\\src\\main\\resources\\test.txt")
          .map(_.split(" ")).map(line =>
              Row(
                line(0),
                line(1),
                line(2),
                line(3),
                line(4),
                line(5)
              )
        )
        //"id", "gender", "age", "location", "advantagedSubject", "disAdvantagedSubject"
        val df = spark.createDataFrame(rawData, schema)
        df.printSchema()
        df.show()
//        println("parquet file read end, start to recommend now")
//        recommend(rawData)
      }*/
      val rawData = spark.read.parquet(srcPath)
      rawData.printSchema()  //test
      println("parquet file read end, start to recommend now")
      val beginTime = System.currentTimeMillis()
      recommend(rawData)
      val times = System.currentTimeMillis() - beginTime
      println("recommend finished : " + times)
    }
    else {
      println("spark session is null. run failed")
    }

  }

  //停止模型训练
  override def stop: Unit = ???

  //获取结果存储绝对路径
  override def getResult: IPackage = {
    results
  }

  var results: UserMLResultPackage = null

  /**
    * 相似矩阵转键值对
    * <uid, recommendString>
    *
    * @param userIDs
    * @param corrMatrix
    * @return
    */
  def transferMatrix2Map(userIDs: Array[Long], corrMatrix: Array[String]): HashMap[Long, String] = {
    val maps = new HashMap[Long, String]
    for (i <- 0 until userIDs.length) {
      maps.put(userIDs(i), corrMatrix(i))
    }
    maps
  }

  def transfer2Package(): UserMLResultPackage = {
    import spark.implicits._
    val cache = spark.read.parquet(cachePath)
    val corrMatrix = cache.map{ line =>
      line(0) + "-" + line(1)
    }.collect()
    new UserMLResultPackage(id, corrMatrix)
  }

  /**
    * 推荐方法
    *
    * @param rawData
    */
  private def recommend( rawData: DataFrame ): Unit = {
    val trainData = buildTrainData(rawData).cache()
    //    trainData.show(20) //test
//    val userIDs = buildUserByID(trainData)
//    println("user Id's length = " + userIDs.length)
//    userIDs.foreach(println)
    if (trainData != null) {
      //计算相似度矩阵
      buildSimilarityMatrix(trainData, 50)
      println("compute similarity matrix end. now transfer to package.")
      results = transfer2Package()
      println("all finish date = " + new Date() + ", the results size = " + results.getResults.size())
    }
    //释放缓存
    trainData.unpersist()
  }

  /**
    * 构建特征训练集
    *
    * @param rawData
    * @return
    */
  private def buildTrainData( rawData: DataFrame ): DataFrame = {
    import spark.implicits._
    import scala.util.control.Breaks._
    val pattern = "[0-9]+".r
    rawData.map{ user =>
    {
      var id: Long = 0L
      breakable{
        if (pattern.findAllIn(user.getAs[String]("id")).toList.size <= 0) break
        id = user.getAs[String]("id").toLong
      }
      User(
        id,
        user.getAs[String]("gender"),
        user.getAs[String]("age"),
        user.getAs[String]("location"),
        user.getAs[String]("advantagedSubject"),
        user.getAs[String]("disAdvantagedSubject")
      )
    }
    }.toDF()
    //    rawData.filter(rawData("id") > -1L).toDF()
  }

  /**
    * 获取所有用户id， 升序排序
    *
    * @param rawData
    * @return
    */
  private def buildUserByID( rawData: DataFrame ): Array[Long] = {
    rawData.groupBy("id").max("id").orderBy("id").rdd.map{line => line(0).asInstanceOf[Long]}.collect()
  }

  /**
    * 构建User信息数组
    *
    * @param trainData
    * @return
    */
  private def buildUserArray( trainData: DataFrame, num: Int ): Array[User] = {
    var datas: Array[Row] = null
    if (num != -1) {
      datas = trainData.collect().take(num)
    }
    else {
      datas = trainData.collect()
    }

    val matrix = new Array[User](datas.length)

    if (trainData != null) {
      /*
        耗性能
       */
//      trainData.createOrReplaceTempView("User")
//      for (i <- 0 until userData.length) {
//        matrix(i) = buildUserModel(trainData.sqlContext.sql("SELECT * FROM User WHERE id = " + userData(i)).first())
//      }
      println("start build user arrays")
      val begin = System.currentTimeMillis()
      var i = 0
      datas.map(user => {
        try {
          matrix(i) = buildUserModel(user)
          i += 1
        } catch {
          case ex: Exception => println(ex.toString)
        }
      })
      println("finish build user arrays : " + (System.currentTimeMillis() - begin))
    }
    else {
      println("trainData is null")
    }

    matrix
  }

  /**
    * 生成用户一一对应的相似度矩阵
    *
    * @return
    */
  private def buildSimilarityMatrix( trainData: DataFrame, N: Int ): Unit = {

    //TODO 对于海量数据，对数据随机分区，每次做全量计算时对不同分区做相似度计算
    val userArr = buildUserArray(trainData, -1)
    println("the numbers of users = " + userArr.length)
    /**
      * TODO 缓兵之计
      * 随机采样1000个样本数据做相似度计算
      */
    var sampleArr: Array[User] = null

    // TODO bug: java.lang.OutOfMemoryError: Java heap space
    //    var matrix = Array.ofDim[UserSimilarity](userData.length, userData.length)
//    val matrix = new Array[String](userArr.length)
    val lists = new util.ArrayList[UserResult]()

    /**
      * 1)开辟有且只有一个UserSimilarity对象
      * 2)开辟一个大小为N的UserSimilarity数组 --> 存放TopK个结果
      */
    val userSim = new UserSimilarity(-1L, 0f)
    val cacheN = new Array[UserSimilarity](N)
    for (i <- 0 until N) {
      cacheN(i) = new UserSimilarity(-1L, 0f)
    }

//    val tempMatrix = new Array[UserSimilarity](userArr.length)

    for (i <- 0 until userArr.length) {
      val userA = userArr(i)
      if (userA != null) {
        try {
          println("begin userA compute : " + userA.id)
          val begin = System.currentTimeMillis()
          if (i % 5000 == 0) {
            sampleArr = buildUserArray(trainData.sample(false, 0.1).toDF(), 1000)
          }
          for (j <- 0 until sampleArr.length) {
            val userB = sampleArr(j)
            if (userB != null) {
              if (userA.id != null && userB.id != null) {
                if (userA.id == userB.id) {
                  //自身相似度
                  userSim.id = userA.id
                  userSim.similarity = 0f
                }
                else {
                  //TODO 加权计算相似度-->优化
                  userSim.id = userB.id
                  userSim.similarity = buildSimilarity(userA, userB)
                }
                //插入TopN数组
                insertTopKCache(cacheN, userSim)
              }
            }
          }
          //耗时：300s
          //take top K into matrix
          //格式：id-{uid1,uid2,...uidN}
          lists.add(new UserResult(userA.id, arr2String(cacheN)))
          //重置TopN数组
          for (i <- 0 until N) {
            cacheN(i) = UserSimilarity(-1L, 0f)
          }
          println("finish userA " + userA.id + " compute : " + (System.currentTimeMillis() - begin))

          if (i % 100000 == 0 && i != 0) {
            try {
              println("i == " + i)
              val df = spark.createDataFrame(lists, UserResult.getClass)
              df.write.mode(SaveMode.Append).parquet(cachePath)
              lists.clear()
            } catch {
              case ex: Exception => println(ex.getLocalizedMessage + "," + ex.getMessage)
            }
          }

        } catch {
          case ex: Exception => println(ex.getMessage + "," + ex.getLocalizedMessage)
        }
      }

//      matrix(i) = userA.id + "-" + getTopK(tempMatrix, 50) // K = 50
    }
    if (lists != null && lists.size() > 0) {
      val df = spark.createDataFrame(lists, UserResult.getClass)
      df.write.mode(SaveMode.Append).parquet(cachePath)
    }
//    matrix
  }

  /**
    * 数组排序转字符串
    * uid1  uid2  uid3  ...  uidN
 *
    * @param cache
    * @return
    */
  private def arr2String( cache: Array[UserSimilarity] ): String = {
    var str = ""
    val arr = cache.toList.sortBy(_.similarity).reverse //升序
    arr.map(value => {
      str += (value.id + "\t")
    })
    str
  }

  //      min = cache.toList.sortBy(_.similarity).head
  /**
    * 比较数组中相似度最小值，插入
 *
    * @param cache
    * @param data
    */
  private def insertTopKCache( cache: Array[UserSimilarity], data: UserSimilarity ) = {
    var min: UserSimilarity = null
    val minValue = Double.MaxValue
    try {
      if (cache != null && !cache.contains(null)) {
        for (i <- 0 until cache.length) {
          if (cache(i) != null) {
            val cacheMinValue = cache(i).similarity
            if (Math.min(minValue, cacheMinValue) == cacheMinValue) {
              min = cache(i)
            }
          }
        }
      }
      if (min != null) {
        if (min.similarity >= data.similarity) {
        }
        else {
          min.id = data.id
          min.similarity = data.similarity
        }
      }
    } catch {
      case ex: Exception => println(ex.getMessage + "<--->" + ex.getLocalizedMessage)
    }
  }

  /**
    * DF 转 User model
    *
    * @param user
    * @return
    */
  private def buildUserModel( user: Row ): User = {
    User(
      user.getAs[Long]("id"),
      user.getAs[String]("gender"),
      user.getAs[String]("age"),
      user.getAs[String]("location"),
      user.getAs[String]("advantagedSubject"),
      user.getAs[String]("disAdvantagedSubject")
    )
  }

  /**
    * 计算一对一的用户相似度
    *
    * @param A
    * @param B
    * @return
    */
  private def buildSimilarity(A: User, B: User): Float = {
    var similarity = 0f

    //计算性别相似度：负相关
    var genderSimilarity = 0f
    if (A.gender.equals(B.gender)) {
      genderSimilarity = 0f
    }
    else{
      genderSimilarity = 1f
    }
    //person计算相似度 --> 过于耗时，每次计算耗时[200,300]毫秒
    /*if (validateDatas(A, B)) {
      //计算科目相似度
      val subjectA: RDD[Double] = sc.parallelize(Array[Double]((A.age).toDouble, (A.advantagedSubject).toDouble, (A.disAdvantagedSubject).toDouble))
      val subjectB: RDD[Double] = sc.parallelize(Array[Double]((B.age).toDouble, (B.disAdvantagedSubject).toDouble, (B.advantagedSubject).toDouble))
      val subjectSimilarity: Double = Statistics.corr(subjectA, subjectB, "pearson")

      //汇总相似度
      similarity = ( genderSimilarity * 0.2f + subjectSimilarity * 0.8f ).toFloat
    }
    else {
      similarity = -1f
    }*/
    //余弦相似度计算
    if (validateDatas(A, B)) {
      similarity = ( genderSimilarity * 0.2f + cosineSimilarity(A, B) * 0.8f ).toFloat
    }
    else {
      similarity = -1f
    }

    similarity
  }

  /**
    * 计算用户间余弦相似度
 *
    * @param A
    * @param B
    * @return
    */
  private def cosineSimilarity(A: User, B: User): Double = {
    val aAge = (A.age).toFloat
    val bAge = (B.age).toFloat
    val aAd = (A.advantagedSubject).toFloat
    val bAd = (B.advantagedSubject).toFloat
    val aDis = (A.disAdvantagedSubject).toFloat
    val bDis = (B.disAdvantagedSubject).toFloat
    //分子 X1Y1 + ... + XnYn
    val molecule = aAge * bAge + aAd * bDis + aDis * bAd
    //分母 sqrt(X1^2 + ... + Xn^2)*sqrt(Y1^2 + ... + Yn^2)
    val denominator = Math.sqrt(aAge * aAge + aAd * aAd + aDis * aDis) * Math.sqrt(bAge * bAge + bAd * bAd + bDis * bDis)

    (molecule / denominator)
  }

  /**
    * 过滤无效数据
    *
    * @param A
    * @param B
    * @return
    */
  def validateDatas(A: User, B: User): Boolean = {
    var isValidate = false
    if (isIntByRegex(A.age) && isIntByRegex(A.advantagedSubject) && isIntByRegex(A.disAdvantagedSubject)
        && isIntByRegex(B.age) && isIntByRegex(B.advantagedSubject) && isIntByRegex(B.disAdvantagedSubject)) {
      isValidate = true
    }
    else {
      isValidate = false
    }
    isValidate
  }

  /**
    * 正则判断字符串是否由纯数字构成
    *
    * @param s
    * @return
    */
  def isIntByRegex(s : String) = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

  /**
    * Top K
    *
    * @param matrix
    * @param N
    * @return 以"\t"为分隔符的字符串
    */
  def getTopK(matrix: Array[UserSimilarity], N: Int): String = {
    var topK: String = ""

    if (matrix != null && N > 0) {
      var num = 0
      if (matrix.length > N)
        num = N
      else
        num = matrix.length

      val topKArr = sc.makeRDD(matrix).sortBy(_.similarity, false).take(num)
      topKArr.foreach(user => {
        topK += (user.id + "\t")
      })
    }

    topK
  }

  /**
    * TopN
    */
//  def getTopN(corrMatrix: Array[Array[UserSimilarity]], N: Int): Array[Array[UserSimilarity]] = {
//    val topN = new Array[Array[UserSimilarity]](corrMatrix.length)
//
//    for (i <- 0 until topN.length) {
//      val group = sc.makeRDD(corrMatrix(i)).sortBy(_.similarity, false).collect()
//      topN(i) = new Array[UserSimilarity](N)
//      Array.copy(group, 0, topN(i), 0, N)
//    }
//
//    topN
//  }


}

object UserRecommender {
  def apply(spark: SparkSession, id: String, srcPath: String, modelPath: String, cachePath: String) =
    new UserRecommender(spark, id, srcPath, modelPath, cachePath)

  def main(args: Array[String]) {
//    val conf = new SparkConf()
//      .setAppName("User Recommendation APP")
//      .setMaster("local[4]")
//    val spark = SparkSession.builder().config(conf).getOrCreate()
//    val recommender = new UserRecommender(spark, null, null)
//    recommender.start
  }
}
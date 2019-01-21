import java.util

import com.jhcomn.lambda.mllib.uhf.UW1000Feature
import org.apache.hadoop.fs.FileSystem
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFPreprocessUtil
import com.jhcomn.lambda.packages.IPackage
import ml.dmlc.xgboost4j.scala.Booster
import org.apache.hadoop.conf.Configuration
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SaveMode


object UhfTest {
  def main(args: Array[String]): Unit = {
    val srcPath = "E:\\PRPSData\\PDMSystemPdmSys_CouplerSPDC0001"
    val ret: util.List[IPackage] = new util.ArrayList[IPackage]()
//    val sparkConf = new SparkConf().setMaster("local[4]").setAppName("XGBoost-spark-example")
//      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//    sparkConf.registerKryoClasses(Array(classOf[Booster]))
//    val sc = new SparkContext(sparkConf)
//    val sc = new SparkContext("master","appName")


//    val uhfUtil = new UHFPreprocessUtil(sc)
    val uhfUtil = new UHFPreprocessUtil()
    val tagId = "1"
    val features = uhfUtil.generateFeatures(srcPath, tagId)

    if (features != null) {
      val featureModel = new UW1000Feature(tagId, features)
      val models = List(featureModel)
      ret.add(featureModel)
      //      try {
      //        //featureModel写入parquet
      //        val df = spark.createDataFrame(models)
      //        //以追加模式持久化新增特征数据
      //        df.write.mode(SaveMode.Append).parquet(parquetPath)
      //      } catch {
      //        case ex: Exception => println(ex)
      //      }
    }
  }
}

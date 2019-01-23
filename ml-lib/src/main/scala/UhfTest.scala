import java.util

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



  }
}

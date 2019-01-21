import ml.dmlc.xgboost4j.scala.spark.XGBoost
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils

/**
  * Created by shimn on 2017/1/17.
  */
class Test {
}

object Test extends App {

//  def main(args: Array[String]) {
//    if (args.length != 3) {
//      println(
//        "usage: program  num_of_rounds training_path model_path")
//      sys.exit(1)
//    }
//    // if you do not want to use KryoSerializer in Spark, you can ignore the related configuration
//    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("XGBoost-spark-example")
//      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//    sparkConf.registerKryoClasses(Array(classOf[Booster]))
//    val sc = new SparkContext(sparkConf)
//    val inputTrainPath = args(1)
//    val outputModelPath = args(2)
//    // number of iterations
//    val numRound = args(0).toInt
//    val trainRDD = MLUtils.loadLibSVMFile(sc, inputTrainPath)
//    // training parameters
//    val paramMap = List(
//      "eta" -> 0.1f,
//      "max_depth" -> 2,
//      "objective" -> "binary:logistic").toMap
//    // use 5 distributed workers to train the model
//    // useExternalMemory indicates whether
//    val model = XGBoost.train(trainRDD, paramMap, numRound, 5, true, true, true, 0.1f)
//    // save model to HDFS path
//    model.saveModelAsHadoopFile(outputModelPath)
//  }

  def isIntByRegex(s : String) = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

  val rates = new Array[Double](3 + 2)
  rates.foreach(println)

//  println(isIntByRegex(null))
//  println(isIntByRegex("\n"))
//  println(isIntByRegex("a123"))
//  println(isIntByRegex("123z"))
//  println(isIntByRegex("12m3"))
}

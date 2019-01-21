package com.jhcomn.lambda.mllib.svmTest

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.util.MLUtils

/**
  * Created by shimn on 2017/12/23.
  */
object svmTest extends App {

  val sparkConf = new SparkConf().setMaster("spark://Master:7077")
  sparkConf.set("spark.cores.max","12")
  val sc = new SparkContext(sparkConf)

  // Load training data in LIBSVM format.
  val data = MLUtils.loadLibSVMFile(sc, "/test_file/605/605.txt").cache()

  // Split data into training (60%) and test (40%).
//  val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
//  val training = splits(0).cache()
//  val test = splits(1)

  val numIters = Array(100, 1000, 10000, 100000, 1000000)
  // Run training algorithm to build the model
  for (i <- 0 until numIters.length) {
    val numIterations = numIters(i)
    val begin = System.currentTimeMillis()
    val model = SVMWithSGD.train(data, numIterations)
    println("numIter = " + numIterations + "; timemills = " + (System.currentTimeMillis() - begin))
  }

}

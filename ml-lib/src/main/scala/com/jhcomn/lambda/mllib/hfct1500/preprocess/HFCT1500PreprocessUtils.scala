package com.jhcomn.lambda.mllib.hfct1500.preprocess

import com.jhcomn.lambda.mllib.hfct1500.Hfct1500PrpdReader
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by longhx on 2017/5/16.
  */
object HFCT1500PreprocessUtils {

  def preprocessBeforeTraining(sc: SparkContext, path: String, fileSystem: FileSystem):Array[Double]= {
    //    val examples = sc.textFile(path).cache()
    //    val data=examples.map(x=>x.split("\\s+").map(_.toDouble))
    //    val train=data.collect()
    val PRPD=new ArrayBuffer[Array[Double]]()
    try {
      val reader = new Hfct1500PrpdReader();
      val train = reader.readHDFS(path, fileSystem)
      val N=train.length
      for(i<-0 until N){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=train(i)(0)
        temp(0)(1)=train(i)(1)*10000
        PRPD++=temp
      }
    } catch {
      case ex: Exception => println(ex)
    };

    val q_num=10
    val fai_num=360
    HFCT_preprocessing.Hfct_featureextract(PRPD,q_num,fai_num)
  }

  def preprocessBeforeClassify(path:String) = {

  }

  def main(args:Array[String]):Unit= {
    val conf=new SparkConf().setMaster("local[2]").setAppName("sparkname")
    val sc=new SparkContext(conf)
    val data_path = "C:\\Users\\shimn\\Downloads\\20132568102743.zip"
    val rdd = sc.textFile(data_path, 1)
    val data = rdd.map(x=>x.split("\\s+").map(_.toByte))
    val train = data.collect()
    println(train.length)
//    val k=preprocessBeforeTraining(sc,data_path)


  }
}

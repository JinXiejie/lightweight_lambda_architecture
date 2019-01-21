package com.jhcomn.lambda.mllib.hfct1500.classify

/**
  * Created by longhx on 2017/5/15.
  */

import breeze.linalg.{CSCMatrix => BSM, DenseMatrix => BDM, DenseVector => BDV, Matrix => BM, SparseVector => BSV, Vector => BV, axpy => brzAxpy, max => Bmax, min => Bmin, sum => Bsum, svd => brzSvd}
import breeze.numerics.abs
import com.jhcomn.lambda.mllib.hfct1500.preprocess.HFCT_preprocessing
import com.jhcomn.lambda.mllib.hfct1500.training.NN.NeuralNetModel
import com.jhcomn.lambda.packages.IPackage
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * 调用HFCT分类的元数据包
  * @param dataPath
  */
case class HFCTClassifyMetaMessage(val dataPath: String) extends IPackage

case class HFCTClassifyResult(val rate: Array[Double]) extends IPackage

object HFCT_classify {

  /**
    * HFCT分类
    * @param data 测试数据
    * @param model 神经网络模型
    * @param tagNum HFCT类别标签个数，不包括“无效数据”和“噪声”两类
    * @return
    */
  def classify(data: Array[Array[Double]], model: NeuralNetModel, tagNum: Int): HFCTClassifyResult = {
    val train = data
    val N = train.length
    val PRPD=Array.ofDim[Double](N,2)
    for(i<-0 until N){
      PRPD(i)(0)=train(i)(0)
      PRPD(i)(1)=train(i)(1)
    }
    val q_num=10
    val fai_num=360
    val fai_aver=360/fai_num
    val xishu=10000
    val PRPD_1=new ArrayBuffer[Array[Double]]()
    val PRPD_2=new ArrayBuffer[Array[Double]]()
    val PRPD_3=new ArrayBuffer[Array[Double]]()
    val PRPD_4=new ArrayBuffer[Array[Double]]()
    var norm_cha_1=0.0
    var norm_cha_2=0.0
    var norm_cha_3=0.0
    var norm_cha_4=0.0
    var norm_cha=0.0
    val yuzhidu1=0.1
    val liangcheng=1
    val zaoshenquyu_PRPD=new ArrayBuffer[Array[Double]]()
    for(i<-0 until N){
      if(abs(PRPD(i)(1))<=yuzhidu1*liangcheng){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        zaoshenquyu_PRPD++=temp
      }
    }

    var norm_cha_zaoshen=0.0
    val N_zaoshen=zaoshenquyu_PRPD.length

    //计算阈值下的噪声方差
    if(N_zaoshen>1){
      norm_cha_zaoshen=HFCT_preprocessing.zaoshenquyu_area_norm_cha(zaoshenquyu_PRPD)
    }

    //过滤阈值下的噪声
    val yuzhidu2=0.1
    val yuzhi=liangcheng*yuzhidu2
    val PRPD_new=new ArrayBuffer[Array[Double]]()

    for(i<-0 until N){
      if(abs(PRPD(i)(1))>yuzhi){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        PRPD_new++=temp
      }
    }
    val N_new=PRPD_new.length
    for(i<-0 until N_new){
      if(PRPD_new(i)(0)<180&&PRPD_new(i)(0)>0&&PRPD_new(i)(1)>0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_1++=temp
      }

      if(PRPD_new(i)(0)>180&&PRPD_new(i)(0)<360&&PRPD_new(i)(1)>0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_2++=temp
      }

      if(PRPD_new(i)(0)<180&&PRPD_new(i)(0)>0&&PRPD_new(i)(1)<0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_3++=temp
      }

      if(PRPD_new(i)(0)>180&&PRPD_new(i)(0)<360&&PRPD_new(i)(1)<0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_4++=temp
      }

    }
    val N_1=PRPD_1.length
    val N_2=PRPD_2.length
    val N_3=PRPD_3.length
    val N_4=PRPD_4.length
    if(N_1>1){
      norm_cha_1=HFCT_preprocessing.PRPD_1_area_norm_cha(PRPD_1)
    }
    if(N_2>1){
      norm_cha_2=HFCT_preprocessing.PRPD_2_area_norm_cha(PRPD_2)
    }
    if(N_3>1){
      norm_cha_3=HFCT_preprocessing.PRPD_3_area_norm_cha(PRPD_3)
    }
    if(N_4>1){
      norm_cha_4=HFCT_preprocessing.PRPD_4_area_norm_cha(PRPD_4)
    }
    norm_cha=(norm_cha_1+norm_cha_2+norm_cha_3+norm_cha_4)/4
    //println(norm_cha) //输出方差

    if((N_1<337&&N_2<337&&N_3<337&&N_4<337)||((norm_cha>0.343)&&(norm_cha<=0.3707))||norm_cha>=0.3832){
      val rates = new Array[Double](tagNum + 2) //试验结果比率数组
      rates(0) = 1.0
      println("invalid data") //tag1
      return new HFCTClassifyResult(rates)
    }
    else if((norm_cha<0.0226)&&(norm_cha_zaoshen<0.0226)&&(norm_cha_zaoshen>0)){
      val rates = new Array[Double](tagNum + 2) //试验结果比率数组
      rates(1) = 1.0
      println("noise") //tag2
      return new HFCTClassifyResult(rates)
    }
    else {
      //非无效数据or噪声，读模型分类
      for(i<-0 until N_new){
        PRPD_new(i)(1)=PRPD_new(i)(1)*xishu
      }
      val K=HFCT_preprocessing.Hfct_featureextract(PRPD_new,q_num,fai_num)  //预处理
      if (K != null) {
        val test_data = new BDM[Double](1, K.length, K.toArray)
        val ouput = HFCT_identify.Hfct_identify_output(test_data, model.weights.toArray).toArray
        val rates = new Array[Double](tagNum + 2) //试验结果比率数组
        val margin = (rates.length - ouput.length)
        for (i <- 0 until ouput.length) {
          rates(i+margin) = ouput(i)
        }
        println(rates)
        return new HFCTClassifyResult(rates)
      }
    }
    null
  }

  def main(args:Array[String]):Unit={
    println("dfa")
    val conf=new SparkConf().setMaster("local").setAppName("sparkname")
    val sc=new SparkContext(conf)
    val data_path = "G:\\HFCT局放分类整理\\hfct图谱分类\\newdatabase\\内部放电\\内部放电-多重的空穴2.txt"
    val examples = sc.textFile(data_path).cache()
    val data=examples.map(x=>x.split("\\s+").map(_.toDouble))
    val train=data.collect()
    val N=train.length
    //.collect()
    /*
    val train_d1 = examples.map { line =>
       val f1 = line.split("\t")
       val f = f1.map(f => f.toDouble)
       val x = f.slice(0, f.length)
       (new BDM(1, x.length, x))
     }

    for(i <-0 to 1)
         println(train(i)(0)+"  "+train(i)(1))
    //数组数据转移到另外一个数组中
    val arr1=new ArrayBuffer[Array[Double]]()
    for(i<-0 to 1) {
      val zhong = Array.ofDim[Double](1, 2)
      zhong(0)(0) = train(i)(0)
      zhong(0)(1) = train(i)(1)
      arr1 ++= zhong.toBuffer   //外面层转化为toBuffer
    }

    for(i<-0 to 1)
      println(arr1(i)(0)+" "+arr1(i)(1))

    */
    val PRPD=Array.ofDim[Double](N,2)
    for(i<-0 until N){
      PRPD(i)(0)=train(i)(0)
      PRPD(i)(1)=train(i)(1)
    }
    val q_num=10
    val fai_num=360
    val fai_aver=360/fai_num
    val xishu=10000
    val PRPD_1=new ArrayBuffer[Array[Double]]()
    val PRPD_2=new ArrayBuffer[Array[Double]]()
    val PRPD_3=new ArrayBuffer[Array[Double]]()
    val PRPD_4=new ArrayBuffer[Array[Double]]()
    var norm_cha_1=0.0
    var norm_cha_2=0.0
    var norm_cha_3=0.0
    var norm_cha_4=0.0
    var norm_cha=0.0
    val yuzhidu1=0.1
    val liangcheng=1
    val zaoshenquyu_PRPD=new ArrayBuffer[Array[Double]]()
    for(i<-0 until N){
      if(abs(PRPD(i)(1))<=yuzhidu1*liangcheng){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        zaoshenquyu_PRPD++=temp
      }
    }

    var norm_cha_zaoshen=0.0
    val N_zaoshen=zaoshenquyu_PRPD.length

    //计算阈值下的噪声方差
    if(N_zaoshen>1){
      norm_cha_zaoshen=HFCT_preprocessing.zaoshenquyu_area_norm_cha(zaoshenquyu_PRPD)
    }

    //过滤阈值下的噪声
    val yuzhidu2=0.1
    val yuzhi=liangcheng*yuzhidu2
    val PRPD_new=new ArrayBuffer[Array[Double]]()

    for(i<-0 until N){
      if(abs(PRPD(i)(1))>yuzhi){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        PRPD_new++=temp
      }
    }
    val N_new=PRPD_new.length
    for(i<-0 until N_new){
      if(PRPD_new(i)(0)<180&&PRPD_new(i)(0)>0&&PRPD_new(i)(1)>0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_1++=temp
      }

      if(PRPD_new(i)(0)>180&&PRPD_new(i)(0)<360&&PRPD_new(i)(1)>0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_2++=temp
      }

      if(PRPD_new(i)(0)<180&&PRPD_new(i)(0)>0&&PRPD_new(i)(1)<0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_3++=temp
      }

      if(PRPD_new(i)(0)>180&&PRPD_new(i)(0)<360&&PRPD_new(i)(1)<0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD_new(i)(0)
        temp(0)(1)=PRPD_new(i)(1)
        PRPD_4++=temp
      }

    }
    val N_1=PRPD_1.length
    val N_2=PRPD_2.length
    val N_3=PRPD_3.length
    val N_4=PRPD_4.length
    if(N_1>1){
      norm_cha_1=HFCT_preprocessing.PRPD_1_area_norm_cha(PRPD_1)
    }
    if(N_2>1){
      norm_cha_2=HFCT_preprocessing.PRPD_2_area_norm_cha(PRPD_2)
    }
    if(N_3>1){
      norm_cha_3=HFCT_preprocessing.PRPD_3_area_norm_cha(PRPD_3)
    }
    if(N_4>1){
      norm_cha_4=HFCT_preprocessing.PRPD_4_area_norm_cha(PRPD_4)
    }
    norm_cha=(norm_cha_1+norm_cha_2+norm_cha_3+norm_cha_4)/4
    println(norm_cha)
    if((N_1<337&&N_2<337&&N_3<337&&N_4<337)||((norm_cha>0.343)&&(norm_cha<=0.3707))||norm_cha>=0.3832){

      println("invalid data") //tag1
    }
    else if((norm_cha<0.0226)&&(norm_cha_zaoshen<0.0226)&&(norm_cha_zaoshen>0)){
      println("noise") //tag2
    }
    else{
      //丢模型
      for(i<-0 until N_new){
        PRPD_new(i)(1)=PRPD_new(i)(1)*xishu
      }
      val K=HFCT_preprocessing.Hfct_featureextract(PRPD_new,q_num,fai_num)
      val test_data=new BDM[Double](1, K.length, K.toArray)
      println("局放")
//---------------------------------------------------------------
      //读取文本中权值矩阵
      val m_1=10
      val n_1=7201
      val m_2=3
      val n_2=11
      val w1=BDM.zeros[Double](m_1,n_1)
      val w2=BDM.zeros[Double](m_2,n_2)

      val source = Source.fromFile("w12.txt")
      val lineIterator = source.getLines
      var i = 0
      var j=0
      for (line <-lineIterator) {
        i=i+1
        j=0
        val x = line.toString.split("\b")
        if(i<m_1+1) {
          for (ca <- x) {
            w1(i-1,j)=ca.toDouble
            j=j+1
          }
        }
        else{
          for (ca <- x) {
            w2(i-1-m_1,j)=ca.toDouble
            j=j+1
          }
        }
      }

      val weights = ArrayBuffer[BDM[Double]]()
      weights+=w1
      weights+=w2
//--------------------------------------------
      val ouput=HFCT_identify.Hfct_identify_output(test_data,weights.toArray)
      println(ouput)




    }

  }
}

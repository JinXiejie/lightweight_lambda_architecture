package com.jhcomn.lambda.mllib.hfct1500.preprocess

/**
  * Created by longhx on 2017/5/15.
  */
import java.lang.Math.floor

import breeze.numerics.abs

import scala.collection.mutable.ArrayBuffer
import scala.math.{pow, sqrt}
/**
  * Created by longhx on 2017/5/8.
  * 放电信号预处理类
  */
object HFCT_preprocessing{
  def Hfct_featureextract(PRPD:ArrayBuffer[Array[Double]],q_num:Int,fai_num:Int): Array[Double]={
    /**
      *提取放电次数特征，输入PRPD为二维数组，外围为ArrayBuffer，行数不定，内部为Array，列数一定。
      * q_num为放电大小区间数，fai_num为放电相位区间数。
      * 输出为ArrayBuffer一维数组
      **/
    val fai_aver=360/fai_num
    val N_length=PRPD.length
    val PRPD_pos=new ArrayBuffer[Array[Double]]()
    val PRPD_neg=new ArrayBuffer[Array[Double]]()
    for(i<-0 until N_length){
      if(PRPD(i)(0)<0){                      //处理边界问题
        PRPD(i)(0)=PRPD(i)(0)+360
      }
      else if(PRPD(i)(0)>=360){             //处理边界问题
        PRPD(i)(0)=PRPD(i)(0)%360
      }
      if(PRPD(i)(1)>0){
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        PRPD_pos++=temp
      }
      else {
        val temp=Array.ofDim[Double](1,2)
        temp(0)(0)=PRPD(i)(0)
        temp(0)(1)=PRPD(i)(1)
        PRPD_neg++=temp
      }
    }
    val N_pos=PRPD_pos.length
    val N_neg=PRPD_neg.length
    for(i<-0 until N_neg){
      PRPD_neg(i)(1)=abs(PRPD_neg(i)(1))
    }
    val K_pos=Array.ofDim[Int](fai_num,q_num)
    val K_neg=Array.ofDim[Int](fai_num,q_num)

    if(N_pos>0){
      val fai_pos=Array.ofDim[Double](N_pos)
      val qs_pos=Array.ofDim[Double](N_pos)
      var q_posmax=PRPD_pos(0)(1)       //最大值
      var q_posmin=PRPD_pos(0)(1)       //最小值
      for(i<-0 until N_pos){
        if(PRPD_pos(i)(1)>q_posmax)
          q_posmax=PRPD_pos(i)(1)
        if(PRPD_pos(i)(1)<q_posmin)
          q_posmin=PRPD_pos(i)(1)
      }
      val q_posaver=(q_posmax-q_posmin+1)/q_num
      for(i<-0 until N_pos){
        fai_pos(i)=floor(PRPD_pos(i)(0)/fai_aver)
        qs_pos(i)=floor((PRPD_pos(i)(1)-q_posmin)/q_posaver)
      }
      var mi=0
      var ni=0
      for(i<-0 until N_pos){
        mi=fai_pos(i).toInt
        ni=qs_pos(i).toInt
        K_pos(mi)(ni)=K_pos(mi)(ni)+1
      }

    }

    if(N_neg>0){
      val fai_neg=Array.ofDim[Double](N_neg)
      val qs_neg=Array.ofDim[Double](N_neg)
      var q_negmax=PRPD_neg(0)(1)       //最大值
      var q_negmin=PRPD_neg(0)(1)       //最小值
      for(i<-0 until N_neg){
        if(PRPD_neg(i)(1)>q_negmax)
          q_negmax=PRPD_neg(i)(1)
        if(PRPD_neg(i)(1)<q_negmin)
          q_negmin=PRPD_neg(i)(1)
      }
      val q_negaver=(q_negmax-q_negmin+1)/q_num
      for(i<-0 until N_neg){
        fai_neg(i)=floor(PRPD_neg(i)(0)/fai_aver)
        qs_neg(i)=floor((PRPD_neg(i)(1)-q_negmin)/q_negaver)
      }
      var mi=0
      var ni=0
      for(i<-0 until N_neg){
        mi=fai_neg(i).toInt
        ni=qs_neg(i).toInt
        K_neg(mi)(ni)=K_neg(mi)(ni)+1
      }
    }
    var K=new ArrayBuffer[Double]()
    for(j<-0 until q_num) {
      for (i <- 0 until fai_num) {
        K += K_pos(i)(j)
      }
    }
    for(j<-0 until q_num) {
      for (i <- 0 until fai_num) {
        K += K_neg(i)(j)
      }
    }

    val k_max=K.max
    for(i<-0 until K.length){
      K(i) = K(i) / k_max
      if (K(i).isNaN)
        return null
    }

    K.toArray
  }

  def area_norm_cha(PRPD:ArrayBuffer[Array[Double]]):Double={
    /**
      *计算某一区域PRPD的标准差
      **/
    val N=PRPD.length
    var norm_cha=0.0
    var sum_fai=0.0
    var sum_q=0.0
    for(i<-0 until N){
      sum_fai+=PRPD(i)(0)
      sum_q+=PRPD(i)(1)
    }
    val mean_fai=sum_fai/N
    val mean_q=sum_q/N
    if(N>1){
      var square_sum=0.0
      var square_cha=0.0
      for(i<-0 until N){
        square_cha=pow(PRPD(i)(0)-mean_fai,2)+pow(PRPD(i)(1)-mean_q,2)
        square_sum+=square_cha
      }
      norm_cha=sqrt(square_sum/N)
    }
    norm_cha
  }

  def zaoshenquyu_area_norm_cha(zaoshenquyu_PRPD:ArrayBuffer[Array[Double]]):Double={
    /**
      *计算划分出的zaoshenquyu_PRPD噪声区域的标准差
      * */
    val N_zaoshen=zaoshenquyu_PRPD.length
    var max_tem1:Double=abs(zaoshenquyu_PRPD(0)(1))
    var norm_cha_zaoshen=0.0
    for(i<-0 until N_zaoshen){
      zaoshenquyu_PRPD(i)(0)=zaoshenquyu_PRPD(i)(0)/180
      zaoshenquyu_PRPD(i)(1)=abs(zaoshenquyu_PRPD(i)(1))
      if(zaoshenquyu_PRPD(i)(1)>max_tem1){
        max_tem1=zaoshenquyu_PRPD(i)(1)
      }
    }
    for(i<-0 until N_zaoshen){
      zaoshenquyu_PRPD(i)(1)=zaoshenquyu_PRPD(i)(1)/max_tem1
    }
    norm_cha_zaoshen=area_norm_cha(zaoshenquyu_PRPD)
    norm_cha_zaoshen
  }

  def PRPD_1_area_norm_cha(PRPD_1:ArrayBuffer[Array[Double]]):Double={
    /**
      *将坐标轴划分出四个区域，计算其中一个区域PRPD_1的标准差
      **/
    val N_PRPD_1=PRPD_1.length
    var max_tem1:Double=abs(PRPD_1(0)(1))
    var norm_cha_PRPD_1=0.0
    for(i<-0 until N_PRPD_1){
      PRPD_1(i)(0)=PRPD_1(i)(0)/180
      PRPD_1(i)(1)=abs(PRPD_1(i)(1))
      if(PRPD_1(i)(1)>max_tem1){
        max_tem1=PRPD_1(i)(1)
      }
    }
    for(i<-0 until N_PRPD_1){
      PRPD_1(i)(1)=PRPD_1(i)(1)/max_tem1
    }
    norm_cha_PRPD_1=area_norm_cha(PRPD_1)
    norm_cha_PRPD_1
  }

  def PRPD_2_area_norm_cha(PRPD_2:ArrayBuffer[Array[Double]]):Double={
    /**
      *将坐标轴划分出四个区域，计算其中一个区域PRPD_2的标准差
      **/
    val N_PRPD_2=PRPD_2.length
    var max_tem1:Double=abs(PRPD_2(0)(1))
    var norm_cha_PRPD_2=0.0
    for(i<-0 until N_PRPD_2){
      PRPD_2(i)(0)=(PRPD_2(i)(0)-180)/180
      PRPD_2(i)(0)=PRPD_2(i)(0)/180
      PRPD_2(i)(1)=abs(PRPD_2(i)(1))
      if(PRPD_2(i)(1)>max_tem1){
        max_tem1=PRPD_2(i)(1)
      }
    }
    for(i<-0 until N_PRPD_2){
      PRPD_2(i)(1)=PRPD_2(i)(1)/max_tem1
    }
    norm_cha_PRPD_2=area_norm_cha(PRPD_2)
    norm_cha_PRPD_2
  }

  def PRPD_3_area_norm_cha(PRPD_3:ArrayBuffer[Array[Double]]):Double={
    /**
      *将坐标轴划分出四个区域，计算其中一个区域PRPD_3的标准差
      **/
    val N_PRPD_3=PRPD_3.length
    var max_tem1:Double=abs(PRPD_3(0)(1))
    var norm_cha_PRPD_3=0.0
    for(i<-0 until N_PRPD_3){
      PRPD_3(i)(0)=PRPD_3(i)(0)/180
      PRPD_3(i)(1)=abs(PRPD_3(i)(1))
      if(PRPD_3(i)(1)>max_tem1){
        max_tem1=PRPD_3(i)(1)
      }
    }
    for(i<-0 until N_PRPD_3){
      PRPD_3(i)(1)=PRPD_3(i)(1)/max_tem1
    }
    norm_cha_PRPD_3=area_norm_cha(PRPD_3)
    norm_cha_PRPD_3
  }

  def PRPD_4_area_norm_cha(PRPD_4:ArrayBuffer[Array[Double]]):Double={
    /**
      *将坐标轴划分出四个区域，计算其中一个区域PRPD_4的标准差
      **/
    val N_PRPD_4=PRPD_4.length
    var max_tem1:Double=abs(PRPD_4(0)(1))
    var norm_cha_PRPD_4=0.0
    for(i<-0 until N_PRPD_4){
      PRPD_4(i)(0)=(PRPD_4(i)(0)-180)/180
      PRPD_4(i)(1)=abs(PRPD_4(i)(1))
      if(PRPD_4(i)(1)>max_tem1){
        max_tem1=PRPD_4(i)(1)
      }
    }
    for(i<-0 until N_PRPD_4){
      PRPD_4(i)(1)=PRPD_4(i)(1)/max_tem1
    }
    norm_cha_PRPD_4=area_norm_cha(PRPD_4)
    norm_cha_PRPD_4
  }
}

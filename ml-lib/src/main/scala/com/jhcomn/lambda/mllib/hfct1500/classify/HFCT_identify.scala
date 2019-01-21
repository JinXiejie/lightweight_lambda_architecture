package com.jhcomn.lambda.mllib.hfct1500.classify

/**
  * Created by longhx on 2017/5/15.
  */
import breeze.linalg.{
Matrix => BM,
CSCMatrix => BSM,
DenseMatrix => BDM,
Vector => BV,
DenseVector => BDV,
SparseVector => BSV,
axpy => brzAxpy,
svd => brzSvd
}

import breeze.numerics.{
exp => Bexp,
tanh => Btanh
}
object HFCT_identify {
  /*
    输入的特征1*7200转变成矩阵，然后在第一个的位置增加一个1,horzcat,水平连接，然后乘以weights(0).T,再用激活函数映射，然后将结果1*10的第一列
    增加一个1，horzcat,水平连接，然后乘以weights(1).T，得到1*3列的矩阵，在用激活函数映射，可得到结果
     */
  def Hfct_identify_output(data:BDM[Double],weights: Array[BDM[Double]]): BDM[Double] =
  {
    val data_bm1 = BDM.ones[Double](1, 1)
    val data_test1 = BDM.horzcat(data_bm1, data)
    val tmpw0 = weights(0)
    val tmpw1 = weights(1)

    val output1_1=data_test1*tmpw0.t
    val output1_2= 1.0 / (Bexp(output1_1 * (-1.0)) + 1.0)

    val data_bm2=BDM.ones[Double](1, 1)
    val data_test2= BDM.horzcat(data_bm2, output1_2)

    val output2_1=data_test2*tmpw1.t
    val output2_2=1.0 / (Bexp(output2_1 * (-1.0)) + 1.0)

    val output2=output2_2.toArray
    val output_gailv=output2_2/output2.sum
    output_gailv
  }
}

package com.jhcomn.lambda.mllib.base

import com.jhcomn.lambda.packages.IPackage

/**
  *
  * Created by shimn on 2017/1/4.
  */
trait IMLTrainingController {

  //开始模型训练并得出结果
  def start

  //停止模型训练
  def stop

  //模型训练是否结束
  def isFinish : Boolean

  //获取当前模型训练进度
  def getProgress : Float

  //获取结果数据包
  def getResult : IPackage

}

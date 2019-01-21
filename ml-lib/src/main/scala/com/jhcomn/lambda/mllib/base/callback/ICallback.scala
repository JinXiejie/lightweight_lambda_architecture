package com.jhcomn.lambda.mllib.base.callback

/**
  * 状态接口回调
  * Created by shimn on 2017/4/20.
  */
trait ICallback {

  /**
    * 开始
    */
  def onStart(msg : String)

  /**
    * 结束
    */
  def onStop(msg : String)

  /**
    * 发生异常，异常信息
    * @param error
    */
  def onError(error : String)

}

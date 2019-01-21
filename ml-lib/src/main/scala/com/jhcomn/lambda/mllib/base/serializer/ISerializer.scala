package com.jhcomn.lambda.mllib.base.serializer

/**
  * 模型序列化接口
  * Created by shimn on 2017/5/16.
  */
trait ISerializer {

  /**
    * 序列化
    * @param obj
    * @param path
    * @tparam T
    */
  def serialize[T](obj: T, path: String)

  /**
    * 反序列化
    * @param path
    * @tparam T
    * @return
    */
  def deserialize[T](path: String): T
}

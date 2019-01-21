package com.jhcomn.lambda.mllib.base.callback

/**
  * 训练模型匿名内部类接口
  * Created by shimn on 2017/4/20.
  */
abstract class TrainingCallback extends ICallback {

  /**
    * 进度更新
    * @param progress
    */
  def onProgress(progress : Float)

  /**
    * 用于覆盖模型：先删除后保存
    * 若未删除即保存，会报错：“org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs:...”
    * @param modelPath  模型路径
    * @param modelBackupPath  备份模型路径
    */
  def onOverwriteModel(modelPath: String, modelBackupPath: String)

}

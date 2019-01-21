package com.jhcomn.lambda.mllib.base

import org.apache.spark.sql.SparkSession

/**
  * Created by shimn on 2017/1/4.
  */
abstract class AbstractMLTrainingController(private val spark: SparkSession,
                                            val id: String,
                                            val srcPath: String,
                                            val modelPath: String,
                                            val cachePath: String) extends IMLTrainingController {

  protected var progress = 0f

  //判断模型训练是够结束
  override def isFinish: Boolean =  progress == 100f

  //获取当前模型训练进度
  override def getProgress : Float = progress

}

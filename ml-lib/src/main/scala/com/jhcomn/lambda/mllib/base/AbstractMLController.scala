package com.jhcomn.lambda.mllib.base

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

/**
  * 机器学习阶段控制器父类
  * Created by shimn on 2017/4/20.
  */
abstract class AbstractMLController(private val spark: SparkSession,
                                    private val fileSystem: FileSystem) extends IMLController {

}

package com.jhcomn.lambda.mllib.base

import com.jhcomn.lambda.mllib.base.callback.{ProcessCallback, TrainingCallback, PreprocessCallback}
import com.jhcomn.lambda.packages.IPackage
import org.apache.hadoop.fs.FSDataInputStream

/**
  * 机器学习阶段控制器接口
  * step1:数据预处理
  * step2:模型训练
  * step3:数据分类/识别
  * Created by shimn on 2017/4/20.
  */
trait IMLController {

  /**
    * step1:数据预处理
    *
    * @param inputs 每个文件的数据流  --> 存在频繁开闭读取流的操作，消耗IO，耗时
    * @param tag  该文件的标签，若无标签则为null
    * @param parquetPath  预处理完毕结构化数据存储的表路径
    * @param callback 数据预处理回调
    */
  def preprocessWithStream(inputs: FSDataInputStream,
                           tag: String,
                           parquetPath: String,
                           callback: PreprocessCallback)

  /**
    * step1:数据预处理
    *
    * @param isTraining 是否为训练集，若是返回null
    * @param srcPath 每个文件的绝对路径  --> 可直接调用sparkContext.textFile()读取
    * @param tag  该文件的标签，若无标签则为null
    * @param parquetPath  预处理完毕结构化数据存储的表路径
    * @param callback 数据预处理回调
    * @return list[pkg] 返回结构化数据列表
    */
  def preprocessWithUrl(isTraining: Boolean,
                        srcPath: String,
                        tag: String,
                        parquetPath: String,
                        callback: PreprocessCallback) : java.util.List[IPackage]

  /**
    * step2:模型训练
    *
    * @param parquetPath  训练数据存储的表路径
    * @param modelPath  模型存储路径
    * @param modelBackupPath  备份模型存储路径
    * @param callback 模型训练回调
    */
  def train(parquetPath: String,
            modelPath: String,
            modelBackupPath: String,
            callback: TrainingCallback)

  /**
    * step3:数据分类/识别
    *
    * @param modelPath  模型存放路径
    * @param data 测试数据
    * @param callback 数据分类/识别回调
    * @return 返回结果
    */
  def analyze(modelPath: String,
              data: IPackage,
              callback: ProcessCallback) : IPackage
}

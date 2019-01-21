package com.jhcomn.lambda.mllib.base.serializer

import java.io.{ObjectInputStream, ObjectOutputStream}

import org.apache.hadoop.fs.{FSDataInputStream, FileSystem, FSDataOutputStream, Path}

/**
  * Created by shimn on 2017/5/16.
  * spark序列化模型到hdfs
  * https://my.oschina.net/waterbear/blog/525347
  */
class BaseSerializer(private val fileSystem: FileSystem) extends ISerializer {
  {
    if (fileSystem == null)
      throw new RuntimeException("fileSystem不能为null！")
  }
  /**
    * 序列化
    *
    * @param obj
    * @param path
    * @tparam T
    */
  override def serialize[T](obj: T, path: String): Unit = {
    if (obj == null || path == null || path.equals("")) {
      println("null exception about serializing in BaseSerializer.")
      return
    }
    val hdfsPath = new Path(path)
    val oos = new ObjectOutputStream(new FSDataOutputStream(fileSystem.create(hdfsPath)))
    oos.writeObject(obj)
    oos.close()
  }

  /**
    * 反序列化
    *
    * @param path
    * @tparam T
    * @return
    */
  override def deserialize[T](path: String): T = {
    if (path == null || path.equals("")) {
      println("null exception about deserializing in BaseSerializer.")
      return (null).asInstanceOf[T]
    }
    val hdfsPath = new Path(path)
    val ois = new ObjectInputStream(new FSDataInputStream(fileSystem.open(hdfsPath)))
    ois.readObject().asInstanceOf[T]
  }
}

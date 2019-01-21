package com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataOutputStreamCallback;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shimn on 2016/12/19.
 */
public interface ReadWriteHdfsDao extends ReadOnlyHdfsDao {
    /**
     * Delete a given path in hdfs
     * @param path an absolute path
     */
    void delete(String path);

    /**
     * Create a folder in hdfs for a give path
     * @param path an absolute path
     */
    void mkdirs(String path);

    /**
     * Write a string to a file in hdfs
     * @param path an absolute path to the file
     * @param contents the contents of the file
     */
    void write(String path, String contents);

    /**
     * Write a file in hdfs using a serializer strategy for handling the hdfs output stream
     * @param path an absolute path to the file
     * @param serializer a strategy for handling an output stream to hdfs
     */
    void write(String path, FSDataOutputStreamCallback serializer);

    /**
     * 上传本地文件到HDFS
     * @param src
     * @param dst
     */
    void copyFromLocalFile(Path src, Path dst);

    /**
     * 基于io流模式上传到hdfs
     * @param inStream
     * @param path
     * @return
     */
    int uploadWithStream(InputStream inStream, String path) throws IOException;
    /**
     * 创建HDFS文件
     * @param file
     * @return
     */
    FSDataOutputStream create(Path file);

    /**
     * 重命名文件
     * @param src
     * @param dst
     */
    void rename(Path src, Path dst);
}

package com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataInputStreamCallback;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

import java.util.List;

/**
 * Created by shimn on 2016/12/19.
 */
public interface ReadOnlyHdfsDao extends HdfsDao{
    /**
     * list paths for a given glob style string expression.
     * <p>
     *
     * See {@link FileSystem#globStatus(org.apache.hadoop.fs.Path)} for more information on globbing.
     *
     * @param path the glob string representing the path expression
     * @return an array of matching {@link FileStatus} items
     */
    FileStatus[] globStatus(String path);

    /**
     * 遍历文件
     * list paths based on exact path matching
     * @param path an absolute path
     * @return an array of child files/directories
     */
    FileStatus[] listStatus(String path);

    /**
     * Get meta-data for a given path
     * @param path an absolute path
     * @return the file system meta data infromation for the path
     */
    FileStatus getFileStatus(String path);

    /**
     * Get summarization of directory, file and size for a given path
     * @param path an abolsute path
     */
    ContentSummary getTotalSummary(String path);

    /**
     * Read the contents of a file to a list of lines. Reads the entire file into memeory (not recommended for large files)
     * @param filePath an absolute path to a file in hdfs
     * @return List of strings representing the entire file
     */
    List<String> readFile(String filePath);

    /**
     * Read the contents of a file using a deserializer capable of handling the input stream
     * @param filePath an absolute path
     * @param deserializer a deserializer implementation providing a strategy to read the file from a stream
     * @return the value resolved by the deserializer implementation
     */
    <T> T readFile(String filePath, FSDataInputStreamCallback<T> deserializer);

    /**
     * Check if a path exists in hdfs
     * @param path an absolute path
     * @return true if the path exists
     */
    boolean exists(String path);

    /**
     * 从HDFS下载文件到本地
     * @param remote
     * @param local
     */
    void download(Path remote, Path local);

    /**
     * 获取文件的最后修改时间
     * @param file
     * @return
     */
    long getLastModifiedTime(Path file);

    /**
     * 读取HDFS某个目录下所有文件
     * @param dir
     * @return
     */
    Path[] listAllFile(Path dir);

    /**
     * 查找文件所在HDFS集群的位置
     * @param file
     * @return
     */
    BlockLocation[] location(Path file);

    /**
     * 获取HDFS集群所有节点信息
     * @return
     */
    DatanodeInfo[] getClusterNodes();
}

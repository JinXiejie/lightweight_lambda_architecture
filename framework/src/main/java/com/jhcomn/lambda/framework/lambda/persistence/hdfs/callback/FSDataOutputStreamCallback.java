package com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback;

import org.apache.hadoop.fs.FSDataOutputStream;

/**
 * Created by shimn on 2016/12/19.
 */
public interface FSDataOutputStreamCallback {

    void serialize(FSDataOutputStream fos);

}

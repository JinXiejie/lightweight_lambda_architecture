package com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback;

import org.apache.hadoop.fs.FSDataInputStream;

/**
 * Created by shimn on 2016/12/19.
 */
public interface FSDataInputStreamCallback<T> {

    T deserialize(FSDataInputStream fsis);

}

package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs;

import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector.Hdfs2ParquetContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HDFS文本文件转为Parquet结构化文件包装类
 * Created by shimn on 2016/12/27.
 */
public abstract class Hdfs2ParquetWrapper {

    private static Logger log = LoggerFactory.getLogger(Hdfs2ParquetWrapper.class);

    protected String date;

    protected Hdfs2ParquetContext parquetContext;

    public Hdfs2ParquetWrapper(String date) {
        this.date = date;
        parquetContext = new Hdfs2ParquetContext();
    }

    public abstract void dispatch();
}

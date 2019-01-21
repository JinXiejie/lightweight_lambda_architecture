package com.jhcomn.lambda.framework.lambda.persistence.hbase.dao;

/**
 * HBase基础操作接口
 * Created by shimn on 2016/12/22.
 */
public interface HBaseDao {

    void init();

    void validate();

    void close();

}

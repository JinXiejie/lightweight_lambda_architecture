package com.jhcomn.lambda.framework.lambda.persistence.hbase.dao;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.RowFilter;

import java.io.IOException;
import java.util.List;

/**
 * HBase增删改查操作接口
 * Created by shimn on 2016/12/22.
 */
public interface HBaseRowDao extends HBaseDao {

    void insert(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException;

    void insert(String tableName, List<Put> puts) throws IOException;

    void delete(String tableName, String rowKey, String columnFamily, String column) throws IOException;

    void delete(String tableName, List<Delete> dels) throws IOException;

    Result query(String tableName, String rowKey, String columnFamily, String column) throws IOException;

    List<Result> queryAll(String tableName) throws IOException;

    List<Result> queryByKeyPrefix(String tableName, String keyPrefix) throws IOException;

    List<Result> queryByKeySubStr(String tableName, String subString) throws IOException;

    List<Result> queryByFilter(String tableName, RowFilter rowFilter) throws IOException;
}

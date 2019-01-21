package com.jhcomn.lambda.framework.lambda.persistence.hbase.dao;

import org.apache.hadoop.hbase.HTableDescriptor;

import java.io.IOException;

/**
 * HBase表操作接口
 * Created by shimn on 2016/12/22.
 */
public interface HBaseTableDao extends HBaseDao {

    boolean isTableOK(String tableName) throws IOException;

    void enableTable(String tableName) throws IOException;

    void disableTable(String tableName) throws IOException;

    void createTable(String tableName, String[] columnFamilys) throws IOException;

    void createOrOverwiteTable(String tableName, String[] columnFamilys) throws IOException;

    void deleteTable(String tableName) throws IOException;

    HTableDescriptor[] listTables() throws IOException;

    void modifyTable(String tableName, String[] addColumnFamilys, String[] delColumnFamilys);
}

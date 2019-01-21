package com.jhcomn.lambda.framework.lambda.persistence.hbase.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseRowDao;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HBase增删改查操作实现类
 * Created by shimn on 2016/12/22.
 */
public class HBaseRowDaoImpl extends HBaseDaoImpl implements HBaseRowDao {

    private static final Logger log = LoggerFactory.getLogger(HBaseRowDaoImpl.class);

    /**
     * insert only one record
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param column
     * @param value
     * @throws IOException
     */
    @Override
    public void insert(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {

        validate();
        if (rowKey == null) {
            log.error("insert failed, rowKey should not be null.");
            return;
        }

        TableName tName = TableName.valueOf(tableName);
        Table table = connection.getTable(tName);

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));

        table.put(put);
        table.close();
    }

    /**
     * insert list of records
     * @param tableName
     * @param puts
     * @throws IOException
     */
    @Override
    public void insert(String tableName, List<Put> puts) throws IOException {

        validate();

        if (puts == null) {
            log.error("insert failed, puts should not be null.");
            return;
        }

        TableName tName = TableName.valueOf(tableName);
        Table table = connection.getTable(tName);

        table.put(puts);
        table.close();
    }

    /**
     * delete only one cell or cells whose rowkey is same
     * @param tableName
     * @param rowKey
     * @param columnFamily if columnFamily is null, delete all cells which has the same rowkey
     * @param column if column is null & columnFamily isn't null, delete all cells which has the same rowkey and columnFamily
     * @throws IOException
     */
    @Override
    public void delete(String tableName, String rowKey, String columnFamily, String column) throws IOException {

        validate();
        if (rowKey == null) {
            log.error("delete failed, rowKey should not be null.");
            return;
        }

        TableName tName = TableName.valueOf(tableName);
        if (!admin.tableExists(tName)) {
            log.info(tableName + " not exists.");
        } else {
            Table table = connection.getTable(tName);
            Delete del = new Delete(Bytes.toBytes(rowKey));
            if (columnFamily != null)
                del.addFamily(Bytes.toBytes(columnFamily));
            if (columnFamily != null && column != null) {
                del.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            }

            table.delete(del);
            table.close();
        }
    }

    /**
     * delete list of records
     * @param tableName
     * @param dels
     * @throws IOException
     */
    @Override
    public void delete(String tableName, List<Delete> dels) throws IOException {

        validate();

        if (dels == null) return;

        TableName tName = TableName.valueOf(tableName);
        if (!admin.tableExists(tName)) {
            log.info(tableName + " not exists.");
        } else {
            Table table = connection.getTable(tName);
            table.delete(dels);
            table.close();
        }
    }

    /**
     * query for datas
     * @param tableName
     * @param rowKey
     * @param columnFamily if columnFamily is null, return all datas which has the same rowKey
     * @param column if column is null, return all datas which has the same rowKey and columnFamily
     * @return
     * @throws IOException
     */
    @Override
    public Result query(String tableName, String rowKey, String columnFamily, String column) throws IOException {

        validate();

        Result result = null;
        if (rowKey == null) {
            log.error("query failed, rowKey should not be null.");
            return null;
        }

        Table table = connection.getTable(TableName.valueOf(tableName));

        Get get = new Get(Bytes.toBytes(rowKey));
        if (columnFamily != null)
            get.addFamily(Bytes.toBytes(columnFamily));
        if (columnFamily != null && column != null)
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        result = table.get(get);

        table.close();

        return result;
    }

    /**
     * query all datas from table
     * @param tableName
     * @return
     * @throws IOException
     */
    @Override
    public List<Result> queryAll(String tableName) throws IOException {

        validate();

        List<Result> results = new ArrayList<Result>();

        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner)
            results.add(result);

        scanner.close();

        return results;
    }

    /**
     * query all which has the same prefix of rowKey
     * @param tableName
     * @param keyPrefix
     * @return
     * @throws IOException
     */
    @Override
    public List<Result> queryByKeyPrefix(String tableName, String keyPrefix) throws IOException {

        validate();

        if (keyPrefix == null) return null;
        List<Result> results = new ArrayList<Result>();

        Table table  = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,
                new BinaryPrefixComparator(keyPrefix.getBytes()));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner)
            results.add(result);

        scanner.close();

        return results;
    }

    /**
     * query all which has the subString of rowKey
     * @param tableName
     * @param subString
     * @return
     * @throws IOException
     */
    @Override
    public List<Result> queryByKeySubStr(String tableName, String subString) throws IOException {

        validate();

        if (subString == null) return null;
        List<Result> results = new ArrayList<Result>();

        Table table  = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,
                new SubstringComparator(subString));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner)
            results.add(result);

        scanner.close();

        return results;
    }

    /**
     * query all by DIY filter
     * @param tableName
     * @param rowFilter
     * @return
     * @throws IOException
     */
    @Override
    public List<Result> queryByFilter(String tableName, RowFilter rowFilter) throws IOException {

        validate();

        if (rowFilter == null) return null;
        List<Result> results = new ArrayList<Result>();

        Table table  = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.setFilter(rowFilter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner)
            results.add(result);

        scanner.close();

        return results;
    }

}

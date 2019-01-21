package com.jhcomn.lambda.framework.lambda.persistence.hbase.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseTableDao;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HBase表操作实现类
 * Created by shimn on 2016/12/22.
 */
public class HBaseTableDaoImpl extends HBaseDaoImpl implements HBaseTableDao {

    private static final Logger log = LoggerFactory.getLogger(HBaseTableDaoImpl.class);

    /**
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    @Override
    public boolean isTableOK(String tableName) throws IOException {
        validate();
        if (tableName == null || tableName.equals(""))
            return false;

        TableName tName = TableName.valueOf(tableName);

        return admin.tableExists(tName) && admin.isTableEnabled(tName);
    }

    @Override
    public void enableTable(String tableName) throws IOException {
        if (isTableOK(tableName))
            return;

        admin.enableTable(TableName.valueOf(tableName));
    }

    @Override
    public void disableTable(String tableName) throws IOException {
        if (isTableOK(tableName)) {
            admin.disableTable(TableName.valueOf(tableName));
        }
    }

    /**
     * create table if not exists
     * @param tableName
     * @param columnFamilys
     * @throws IOException
     */
    @Override
    public void createTable(String tableName, String[] columnFamilys) throws IOException {
        validate();
        if (columnFamilys == null) {
            log.error("createTable failed, columnFamilys should not be null.");
            return;
        }

        TableName tName = TableName.valueOf(tableName);
        if (admin.tableExists(tName)) {
            log.info(tableName + " exists.");
        } else {
            HTableDescriptor hTableDesc = new HTableDescriptor(tName);
            for (String col : columnFamilys) {
                HColumnDescriptor hColumnDesc = new HColumnDescriptor(col);
                hTableDesc.addFamily(hColumnDesc);
            }
            admin.createTable(hTableDesc);
        }
    }

    /**
     * create or overwrite table if exists
     * @param tableName
     * @param columnFamilys
     * @throws IOException
     */
    @Override
    public void createOrOverwiteTable(String tableName, String[] columnFamilys) throws IOException {
        validate();
        if (columnFamilys == null) {
            log.error("createOrOverwiteTable failed, columnFamilys should not be null.");
            return;
        }

        deleteTable(tableName);

        createTable(tableName, columnFamilys);
    }

    /**
     * delete table if exists
     * @param tableName
     * @throws IOException
     */
    @Override
    public void deleteTable(String tableName) throws IOException {
        validate();

        TableName tName = TableName.valueOf(tableName);
        if (admin.tableExists(tName)) {
            admin.disableTable(tName);
            admin.deleteTable(tName);
        } else {
            log.info(tableName + " not exists.");
        }
    }

    /**
     * list all tables description
     * @return
     * @throws IOException
     */
    @Override
    public HTableDescriptor[] listTables() throws IOException {
        validate();

        HTableDescriptor[] hTableDescriptors = null;
        hTableDescriptors = admin.listTables();
        return hTableDescriptors;
    }

    /**
     * 修改表结构
     * @param tableName
     * @param addColumnFamilys 如果为空则不添加
     * @param delColumnFamilys 如果为空则不删除
     */
    @Override
    public void modifyTable(String tableName, String[] addColumnFamilys, String[] delColumnFamilys) {

        validate();

        TableName tName = TableName.valueOf(tableName);
        try {
            if (admin.tableExists(tName)) {
                admin.disableTable(tName);
                //remove column familys
                if (delColumnFamilys != null)
                    for (String cf : delColumnFamilys) {
                        admin.deleteColumn(tName, Bytes.toBytes(cf));
                    }
                //add column familys
                if (addColumnFamilys != null)
                    for (String cf : addColumnFamilys) {
                        HColumnDescriptor hcd = new HColumnDescriptor(cf);
                        admin.addColumn(tName, hcd);
                    }

                //enable table
                admin.enableTable(tName);
            }
            else {
                log.info(tableName + " not exists.");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (admin.tableExists(tName))
                    admin.enableTable(tName);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}

package com.jhcomn.lambda.framework.lambda.persistence.hbase.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hbase.HBaseHelper;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseDao;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HBase操作基础实现类
 * Created by shimn on 2016/12/22.
 */
public class HBaseDaoImpl implements HBaseDao {

    private static final Logger log = LoggerFactory.getLogger(HBaseDaoImpl.class);

    protected Connection connection = null;
    protected Admin admin = null;

    public HBaseDaoImpl() {
        init();
    }

    /**
     * initial connection and admin
     */
    @Override
    public void init() {
        try {
            if (connection == null) {
                connection = HBaseHelper.getConnection();
            }
            if (connection != null && admin == null) {
                admin = connection.getAdmin();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void validate() {
        if (connection == null || admin == null) {
            throw new RuntimeException(
                    "connection is null || admin is null"
            );
        }
    }

    /**
     * close hbase connection
     */
    @Override
    public void close() {
        try {
            if (null != admin) {
                admin.close();
                admin = null;
            }
            if (null != connection) {
                HBaseHelper.close();
                connection = null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

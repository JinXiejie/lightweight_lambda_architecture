package com.jhcomn.lambda.framework.lambda.persistence.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HBase工具类
 * Created by shimn on 2016/12/22.
 */
public class HBaseHelper {

    private static final Logger log = LoggerFactory.getLogger(HBaseHelper.class);

    //http://ifeve.com/from-singleton-happens-before/
    private static volatile Connection connection = null;

    /**
     * double-checked get HBASE connection instance
     * @return
     */
    public static Connection getConnection() {
        if (connection == null) {
            synchronized (HBaseHelper.class) {
                if (connection == null) {
                    try {
                        //initial configuration
                        Configuration conf = HBaseConfiguration.create();
                        conf.addResource(new Path(System.getenv("HBASE_HOME"), "/conf/hbase-site.xml"));
                        //create connection
                        return ConnectionFactory.createConnection(conf);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    /**
     * close HBase connection
     */
    public static void close() {
        if (connection != null) {
            synchronized (HBaseHelper.class) {
                try {
                    connection.close();
                    connection = null;
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}

package com.jhcomn.lambda.framework.lambda.persistence.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL连接控制类
 * Created by shimn on 2017/4/24.
 */
public class MySQLHelper {

    private static volatile Connection connection;

    public static Connection getConnection() throws Exception {
        if (connection == null) {
            synchronized (MySQLHelper.class) {
                if (connection == null) {
                    Class.forName(SQLProperties.MYSQL_DRIVER);
                    connection = DriverManager.getConnection(
                            SQLProperties.MYSQL_URL,
                            SQLProperties.MYSQL_USERNAME,
                            SQLProperties.MYSQL_PASSWORD);
                    System.out.println("MySQL数据库连接成功！");
                }
            }
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            synchronized (MySQLHelper.class) {
                if (connection != null) {
                    try {
                        connection.close();
                        connection = null;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

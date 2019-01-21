package com.jhcomn.lambda.framework.lambda.sync.base;


import com.jhcomn.lambda.framework.lambda.persistence.mysql.MySQLJdbcUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 同步模型控制器父类
 * Created by shimn on 2017/4/24.
 */
public abstract class AbstractSyncController implements ISyncController {

    protected MySQLJdbcUtils jdbcUtils = null;

    public AbstractSyncController() {
        jdbcUtils = MySQLJdbcUtils.getInstance();
    }

    public void delete(String tableName, String id) {
        String sql = "delete from " + tableName + " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            if (jdbcUtils.updateByPreparedStatement(sql, params)) {
                System.out.println("delete id = " + id + " from " + tableName + ", 成功...");
            } else {
                System.out.println("delete id = " + id + " from " + tableName + ", 失败...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void close() {
        jdbcUtils.release();
        //暂且不关闭数据库连接
    }
}

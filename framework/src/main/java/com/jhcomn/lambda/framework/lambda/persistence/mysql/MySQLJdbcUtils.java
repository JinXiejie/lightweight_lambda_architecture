package com.jhcomn.lambda.framework.lambda.persistence.mysql;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * jdbc操作sql工具类
 * 参考：http://blog.csdn.net/yanzi1225627/article/details/26950615
 * Created by shimn on 2017/4/25.
 */
public class MySQLJdbcUtils {

    private static volatile MySQLJdbcUtils jdbcUtils = null;
    private Connection connection;
    private PreparedStatement pState;
    private ResultSet resultSet;

    public static MySQLJdbcUtils getInstance() {
        if (jdbcUtils == null) {
            synchronized (MySQLJdbcUtils.class) {
                if (jdbcUtils == null)
                    jdbcUtils = new MySQLJdbcUtils();
            }
        }
        return jdbcUtils;
    }

    private MySQLJdbcUtils() {
        validate();
    }

    public boolean validate() {
        boolean isValid = false;
        try {
            if (connection != null)
                isValid = connection.isValid(1);
            System.out.println("mysql connection is valid : " + isValid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection == null || !isValid) {
            System.out.println("mysql connection is null! 获取数据库连接......");
            connection = null;
            try {
                connection = MySQLHelper.getConnection();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 增、删、改
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public boolean updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
        boolean flag = false;
        int result = -1;

        validate();

        pState = connection.prepareStatement(sql);
        int index = 1;
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++)
                pState.setObject(index++, params.get(i));
        }
        result = pState.executeUpdate();
        flag = result > 0 ? true : false;
        return flag;
    }

    /**
     * 查询单条记录
     * @param sql
     * @param params
     * @return map<column, value>
     * @throws SQLException
     */
    public Map<String, Object> findSingleResult(String sql, List<Object> params) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        int index = 1;

        validate();

        pState = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pState.setObject(index++, params.get(i));
            }
        }
        resultSet = pState.executeQuery();//返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while(resultSet.next()){
            for(int i=0; i<col_len; i++ ){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        return map;
    }

    /**查询多条记录
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> findMuliResult(String sql, List<Object> params) throws SQLException{
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int index = 1;

        validate();

        pState = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pState.setObject(index++, params.get(i));
            }
        }
        resultSet = pState.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i=0; i<cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }
        return list;
    }

    /**通过反射机制查询单条记录
     * @param sql
     * @param params
     * @param cls
     * @return
     * @throws Exception
     */
    public <T> T findSingleRefResult(String sql, List<Object> params,
                                     Class<T> cls )throws Exception{
        T resultObject = null;
        int index = 1;

        validate();

        pState = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pState.setObject(index++, params.get(i));
            }
        }
        resultSet = pState.executeQuery();
        ResultSetMetaData metaData  = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            //通过反射机制创建一个实例
            resultObject = cls.newInstance();
            for(int i = 0; i < cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);
                field.setAccessible(true); //打开javabean的访问权限
                field.set(resultObject, cols_value);
            }
        }
        return resultObject;

    }

    /**通过反射机制查询多条记录
     * @param sql
     * @param params
     * @param cls
     * @return
     * @throws Exception
     */
    public <T> List<T> findMuliRefResult(String sql, List<Object> params,
                                         Class<T> cls )throws Exception {
        List<T> list = new ArrayList<T>();
        int index = 1;

        validate();

        pState = connection.prepareStatement(sql);
        if(params != null && !params.isEmpty()){
            for(int i = 0; i<params.size(); i++){
                pState.setObject(index++, params.get(i));
            }
        }
        resultSet = pState.executeQuery();
        ResultSetMetaData metaData  = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            //通过反射机制创建一个实例
            T resultObject = cls.newInstance();
            for(int i = 0; i<cols_len; i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_value = resultSet.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);
                field.setAccessible(true); //打开javabean的访问权限
                field.set(resultObject, cols_value);
            }
            list.add(resultObject);
        }
        return list;
    }

    /**
     * 释放数据库资源
     */
    public void release() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        if (connection != null) {
            MySQLHelper.close();
            connection = null;
        }
    }

    public static void main(String[] args) {
//        MySQLJdbcUtils jdbcUtils = MySQLJdbcUtils.getInstance();
//        jdbcUtils.validate();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        jdbcUtils.closeConnection();
//        jdbcUtils.validate();

        /*******************增*********************/
        /*String sql = "insert into users (username, pswd) values (?, ?), (?, ?)";
        List<Object> params = new ArrayList<>();
        params.add("小明");
        params.add("123");
        params.add("李雷");
        params.add("123");
        try {
            boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*******************删*********************/
        //删除名字为李雷的记录
        /*String sql = "delete from users where username = ?";
        List<Object> params = new ArrayList<>();
        params.add("李雷");
        try {
            boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*******************改*********************/
        //将名字为李雷的密码改了
        /*String sql = "update users set pswd = ? where username = ?";
        List<Object> params = new ArrayList<>();
        params.add("321");
        params.add("李雷");
        try {
            boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*******************查*********************/
        //不利用反射查询多个记录
		/*		String sql2 = "select * from userinfo ";
		List<Map<String, Object>> list = jdbcUtils.findModeResult(sql2, null);
		System.out.println(list);*/

        //利用反射查询 单条记录
		/*		String sql = "select * from userinfo where username = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add("李四");
		UserInfo userInfo;
		try {
			userInfo = jdbcUtils.findSimpleRefResult(sql, params, UserInfo.class);
			System.out.print(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
    }
}

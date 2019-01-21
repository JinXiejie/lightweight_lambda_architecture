package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector.impl;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataInputStreamCallback;
import com.jhcomn.lambda.packages.test.model.User;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TestDispatcher解析处理
 * Created by shimn on 2017/1/3.
 */
public class TestFSDataInputStreamCallbackImpl implements FSDataInputStreamCallback<Void> {

    private static final Logger log = LoggerFactory.getLogger(TestFSDataInputStreamCallbackImpl.class);

    private SparkSession spark;

    private String path;

    public TestFSDataInputStreamCallbackImpl(SparkSession spark, String path) {
        this.spark = spark;
        this.path = path;
    }

    @Override
    public Void deserialize(FSDataInputStream fsis) {
        List<User> lists = new ArrayList<User>();
        BufferedReader reader = null;
        try {
//            System.out.println("TestFSDataInputStreamCallbackImpl deserialize now");
            reader = new BufferedReader(new InputStreamReader(fsis));
            String str = "";
            int rowIndex = 0;
            User user = new User();
            while ((str = reader.readLine()) != null) {
//                System.out.println("read line now");
                if (str.equals("")) {
                    if (lists != null && lists.size() >= ConstantDatas.PARQUET_FLUSH_SIZE) {
//                                    System.out.println("append to parquet file");
                        Dataset<Row> userDF = spark.createDataFrame(lists, User.class);
//                                    userDF.write().mode(SaveMode.Append).saveOrUpdate(path + Properties.PARQUET_SPEED_DATA_PATH + "/user.parquet"); // /user_table
//                                    userDF.printSchema();
                        userDF.write().mode(SaveMode.Append).parquet(path);
                        lists.clear();
                    }
                    if (user != null && rowIndex > 0) {
                        lists.add(user);
                        user = new User();
                    }
                    rowIndex = 0;

                    continue;
                }
                else {
                    insertByRowIndex(user, rowIndex, str);
                    rowIndex++;
                }
            }
            //flush tail of all
            if (lists != null && lists.size() > 0) {
                Dataset<Row> userDF = spark.createDataFrame(lists, User.class);
//                            userDF.write().mode(SaveMode.Append).saveOrUpdate(path + Properties.PARQUET_SPEED_DATA_PATH + "/user.parquet");
                userDF.write().mode(SaveMode.Append).parquet(path);
//                System.out.println("append to parquet file last");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;

    }

    Random random = new Random();

    /**
     * ugly function to insert a user row by row
     * @param user
     * @param index
     * @param str
     */
    private void insertByRowIndex(User user, int index, String str) {
        if (user == null || index < 0) return;
        switch (index) {
            case 0: user.setId(str); break;
            case 1: user.setAccount(user.getId() + "@qq.com"); break;
            case 2: user.setCity(str); break;
            case 3: user.setAge(random.nextInt(5) + ""); break;
            case 5: user.setLocation(str); break;
            case 6: user.setProvince(str); break;
            case 7: user.setAdvantagedSubject(random.nextInt(10) + ""); break;
            case 8: user.setDisAdvantagedSubject(random.nextInt(10) + ""); break;
            case 9: {
                if (str.equals("m"))
                    user.setGender("0");
                else
                    user.setGender("1");
            } break;
            case 10: user.setLastTime(str); break;
        }
    }
}

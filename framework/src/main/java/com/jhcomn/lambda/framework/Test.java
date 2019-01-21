package com.jhcomn.lambda.framework;

import com.jhcomn.lambda.framework.lambda.batch.persistence.hdfs.BatchHdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.utils.StringUtil;

import java.util.Date;

/**
 * Created by shimn on 2017/1/16.
 */
public class Test {
    public static void main(String[] args) {
//        BatchHdfs2ParquetWrapper wrapper = new BatchHdfs2ParquetWrapper(
//                StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"),
//                new BatchUpdatePkg("1", "TEST"));
//        wrapper.dispatch();
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("con't find the driver");
            e.printStackTrace();
        }
    }
}

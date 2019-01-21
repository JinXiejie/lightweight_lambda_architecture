package com.jhcomn.lambda.framework.lambda.base.utils;

import com.jhcomn.lambda.framework.lambda.batch.persistence.hdfs.BatchHdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.utils.StringUtil;

import java.util.Date;

/**
 * 一个topic一个并发任务
 * Created by shimn on 2017/1/16.
 */
public class ScheduledTask implements Runnable {

    private String uid;

    private String topic;

    public ScheduledTask(String uid, String topic) {
        this.uid = uid;
        this.topic = topic;
    }

    @Override
    public void run() {
        System.out.println(this.toString() + " execute now.");
        BatchHdfs2ParquetWrapper wrapper = new BatchHdfs2ParquetWrapper(
                StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"),
                new BatchUpdatePkg(uid, topic));
        wrapper.dispatch();
    }

    @Override
    public String toString() {
        return "ScheduledTask{" +
                "uid='" + uid + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}

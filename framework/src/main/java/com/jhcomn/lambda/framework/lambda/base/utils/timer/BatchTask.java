package com.jhcomn.lambda.framework.lambda.base.utils.timer;

import com.jhcomn.lambda.framework.lambda.batch.persistence.hdfs.BatchHdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.utils.StringUtil;

import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;

/**
 * 定时批处理任务
 * Created by shimn on 2016/12/30.
 */
public class BatchTask extends TimerTask {

    //算法模型客户id
    private String uid;

    //算法模型主题
    private Collection<String> mlTopics;

    public BatchTask(Collection<String> mlTopics) {
        this.mlTopics = mlTopics;
    }

    public BatchTask(String uid, Collection<String> mlTopics) {
        this(mlTopics);
        this.uid = uid;
    }

    @Override
    public void run() {
        for (String topic : mlTopics) {
            /**
             * 并发执行模型迭代运算
             */
            //TODO 可能需要优化此机制
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BatchHdfs2ParquetWrapper wrapper = new BatchHdfs2ParquetWrapper(
                            StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"),
                            new BatchUpdatePkg(uid, topic));
                    wrapper.dispatch();
                }
            }).start();
        }
    }
}

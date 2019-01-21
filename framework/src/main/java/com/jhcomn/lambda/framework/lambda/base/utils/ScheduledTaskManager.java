package com.jhcomn.lambda.framework.lambda.base.utils;

import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程池定时执行批处理任务
 * Created by shimn on 2017/1/16.
 */
public class ScheduledTaskManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskManager.class);

    private static final String INIT_TIME = Properties.BATCH_WORK_TIME; //启动时间，晚上8点
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000; //24小时一个周期

    private static final int THREAD_NUMS = 5; //暂时写死开辟5个线程

    private static volatile ScheduledTaskManager manager = null;
    private volatile ScheduledExecutorService executor = null;

    private ScheduledTaskManager() {
        executor = Executors.newScheduledThreadPool(THREAD_NUMS);
    }

    public static ScheduledTaskManager getInstance() {
        if (manager == null) {
            synchronized (ScheduledTaskManager.class) {
                if (manager == null) {
                    manager = new ScheduledTaskManager();
                }
            }
        }
        return manager;
    }

    /**
     * 启动定时任务
     * @param uid
     * @param topics
     */
    public void execute(String uid, Collection<String> topics) {
        //根据topic数量hash执行时间起始点
        int size = topics != null ? topics.size() : 0;
        long margin = PERIOD_DAY / size; //平均分起始时间点

        if (executor == null) {
            log.error("ScheduledExecutorService is null");
            return;
        }
        else {
            long initDelay  = StringUtil.getTimeMillis(INIT_TIME) - System.currentTimeMillis();
            initDelay = initDelay > 0 ? initDelay : PERIOD_DAY + initDelay;
            int index = 0;
            for (String topic : topics) {
                initDelay += (index++) * margin;
                executor.scheduleAtFixedRate(
                        new ScheduledTask(uid, topic),
                        initDelay,
                        PERIOD_DAY,
                        TimeUnit.MILLISECONDS
                );
            }
        }
    }

}

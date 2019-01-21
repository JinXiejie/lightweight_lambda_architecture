package com.jhcomn.lambda.framework.lambda.base.utils.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JVM系统内存状况监控器
 * Created by shimn on 2017/1/18.
 */
public class JMXUpdateMonitor {

    private static final Logger log = LoggerFactory.getLogger(JMXUpdateMonitor.class);

    private static volatile JMXUpdateMonitor monitor = null;

    private volatile boolean isContinue = false;
    private MonitorThread mission = null;

    /**
     * 是否可继续开辟新任务
     */
    private volatile boolean isUsable = true;

    public static JMXUpdateMonitor getInstance() {
        if (monitor == null) {
            synchronized (JMXUpdateMonitor.class) {
                if (monitor == null) {
                    monitor = new JMXUpdateMonitor();
                }
            }
        }
        return monitor;
    }

    /**
     * 启动监控
     */
    public synchronized void start() {
        if (mission == null) {
            mission = new MonitorThread();
            isContinue = true;
            new Thread(mission).start();
        }
    }

    /**
     * 停止监控
     */
    public synchronized void stop() {
        isContinue = false;
        mission = null;
    }

    /**
     * 返回JVM内存是否足够启动新任务
     * @return
     */
    public synchronized boolean isUsable() {
        return isUsable;
    }

    /**
     * 监控线程
     * 0.3参数可调
     */
    class MonitorThread implements Runnable {

        @Override
        public void run() {
            Runtime runtime = Runtime.getRuntime();
            long max, usable = 0L;
            while (isContinue) {
                max = runtime.maxMemory();
                usable = max - runtime.totalMemory() + runtime.freeMemory();
                if (usable < (max * 0.3f)) {
                    isUsable = false;
                }
                else {
                    isUsable = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

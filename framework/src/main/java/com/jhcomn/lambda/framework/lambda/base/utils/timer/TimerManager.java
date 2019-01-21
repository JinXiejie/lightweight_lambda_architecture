package com.jhcomn.lambda.framework.lambda.base.utils.timer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;

/**
 * 定时器
 * Created by shimn on 2016/12/30.
 */
public class TimerManager {

    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000; //24小时一个周期

    private static volatile TimerManager manager = null;

    public static TimerManager getInstance() {
        if (manager == null) {
            synchronized (TimerManager.class) {
                if (manager == null) {
                    manager = new TimerManager();
                }
            }
        }
        return manager;
    }

    Timer timer = null;
    private TimerManager() {
        timer = new Timer();
    }

    /**
     * 开始定时任务
     * @param topics
     */
    public void start(String uid, Collection<String> topics) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17); //凌晨1点
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行
        if (date.before(new Date())) {
            date = this.addDay(date, 1);
        }

        //新建批处理任务，传入主题
        BatchTask task = new BatchTask(uid, topics);
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行
        timer.schedule(task, date, PERIOD_DAY);
    }

    /**
     * 停止定时任务
     */
    public void stop() {
        if (timer != null)
            timer.cancel();
        manager = null;
    }

    //增加天数
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}

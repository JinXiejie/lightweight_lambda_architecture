package com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit;

//import com.mip.biz.machineLearning.bean.AnalyseResult;
//import com.mip.remoteservice.mlinterface.service.impl.AnalyseTask;
//import com.mip.remoteservice.mq.receiver.KafkaReceiverInterface;
//import com.mip.remoteservice.mq.receiver.callback.IResultCallback;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by jinxiejie on 2017/5/3.
 */
public class KafkaReceiver implements KafkaReceiverInterface {
//    private static String Localhost_Id = "10.100.3.88:9092;10.100.3.89:9092;10.100.3.90:9092";
    private static String Localhost_Id = "127.0.0.1:2181";
    private static Properties props = null;
    private static volatile KafkaReceiver receiver = null;
    private static final int Pools_Nums = 1;

    private volatile boolean isContinue = true;

    private List<ConsumerThread> consumerList = new ArrayList<>();

    AnalyseTask task = new AnalyseTask();

    private KafkaReceiver() {
        isContinue = true;
        props = new Properties();
        props.put("bootstrap.servers", Localhost_Id);
        props.put("group.id", "123");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "latest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    //double-checked
    public static KafkaReceiver getInstance() {
        if (receiver == null) {
            synchronized (KafkaReceiver.class) {
                if (receiver == null) {
                    receiver = new KafkaReceiver();
                }
            }
        }
        return receiver;
    }

    public void Receive() {
        ArrayList<String> topics = new ArrayList<>();
//        topics.add(Topics.UW);
        topics.add("INPUT");
//        topics.add(Topics.TEV);
//        topics.add(Topics.INFRARED);
//        topics.add(Topics.HFCT);
//        topics.add(Topics.TEST);
//        topics.add(Topics.HELLO_WORLD);
        ExecutorService pool = Executors.newFixedThreadPool(Pools_Nums);
        for(String topic : topics){
            ConsumerThread thread = new ConsumerThread(topic);
            consumerList.add(thread);
            pool.execute(thread);
        }
    }

    private Map<String, AnalyseTask> taskMap = new ConcurrentHashMap<>();
    public void addTask(AnalyseTask task) {
        this.task = task;
        taskMap.put(task.id, task);
    }

    public class ConsumerThread extends Thread
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        public String topic;

        // 回调接口
        private volatile IResultCallback callback;

        // 缓存队列
        private volatile Queue<AnalyseResult> queue;

        private ConsumerThread(String topic){
            this.topic = topic;
        }

        public synchronized void setCallback(IResultCallback callback) {
            this.callback = callback;
            this.queue = new LinkedBlockingDeque<>();
        }

        public void run() {
            consumer.subscribe(Arrays.asList(topic));
            System.out.println("启动线程： "+Thread.currentThread().getName());
            while (isContinue) {
                //每次取100条信息
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    //record.key()为json串的唯一标识id
                    String msgId = record.key();
                    if (taskMap.containsKey(msgId)){
                        try {
                            System.out.printf("topic = %s, offset = %d, key = %s, value = %s", record.topic(), record.offset(), record.key(), record.value());
                            System.out.println();
                            boolean isSuccess = queue.offer(new AnalyseResult(record.value()));
                            if (!isSuccess) {
                                System.out.println("topic = " + topic + "的队列已满");
                                break;
                            }
                            AnalyseResult result = queue.peek();
                            if (result != null)
                                queue.poll();
                            task.result = result;
                            taskMap.get(msgId).notify();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        System.out.println("分析结果调用失败！");
                }
            }
        }
    }

    public void release() {
        isContinue = false;
        receiver = null;
    }
    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public List<ConsumerThread> getConsumerList() {
        return consumerList;
    }

    public void setConsumerList(List<ConsumerThread> consumerList) {
        this.consumerList = consumerList;
    }

    public Map<String, AnalyseTask> getTaskMap(){
        return taskMap;
    }
    public AnalyseResult removeMsg(String key){
        AnalyseResult result = new AnalyseResult();
        result = taskMap.get(key).result;
        taskMap.remove(key);
        return result;
    }
}

package com.jhcomn.lambda.framework.kafka.util.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Kafka Consumer基类
 * Created by shimn on 2016/11/25.
 */
public class AbstractConsumer implements IConsumer {

    private static final String TAG = "AbstractConsumer";
    private static final Logger log = LoggerFactory.getLogger(AbstractConsumer.class);

    private KafkaConsumer<String, String> consumer;
    //fetch messages thread
    private fetchMessagesThread fetchThread;
    private boolean isFetch = false;

    public AbstractConsumer() {
        this(null, null);
    }

    public AbstractConsumer(String brokerLists, String groupId) {
        Properties props = new Properties();

        //bootstrapping list of brokers
        if (brokerLists == null)
            brokerLists = "localhost:9092";
        props.put("bootstrap.servers", brokerLists);

        //assigns an individual consumer to a group
        if (groupId == null)
            groupId = "jhcomn";
        props.put("group.id", groupId); //三个节点属于同一组

        //enable auto commit for offsets if the value is true, otherwise not committed
        props.put("enable.auto.commit", "true");
        //return how often updated consumed offsets are written to ZooKeeper
        props.put("auto.commit.interval.ms", "1000");
        //indicates how many milliseconds Kafka will wait for the ZooKeeper to respond to a request (read or write) before giving up and continuing to consume messages
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        //实例化Kafka消费者
        consumer = new KafkaConsumer<String, String>(props);
    }

    @Override
    public void subscribe(ArrayList<String> topics) {
        if (consumer == null) {
            log.error("KafkaConsumer没有实例化");
            return;
        }
        if (topics == null) {
            log.error("KafkaConsumer订阅的主题为空，无法进行订阅操作");
            return;
        }

        consumer.subscribe(topics);
        log.info("KafkaConsumer成功订阅主题数量：" + topics.size());
    }

    @Override
    public void unsubscibe() {
        //取消订阅所有主题
        if (consumer != null)
            consumer.unsubscribe();
    }

    @Override
    public void start() {
        isFetch = true;
        fetchThread = new fetchMessagesThread();
        fetchThread.start();
    }

    @Override
    public void stop() {
        isFetch = false;
        fetchThread = null;
    }

    /*
     * fetch消息线程
     */
    class fetchMessagesThread extends Thread {
        @Override
        public void run() {
            if (consumer != null) {
                while (isFetch) {
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    //TODO 获取数据后的操作
                    for (ConsumerRecord<String, String> record : records)
                        System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                }
            }
        }
    }
}

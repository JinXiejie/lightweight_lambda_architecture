package com.jhcomn.lambda.framework.lambda.base.input.kafka.connectors;

import com.jhcomn.lambda.framework.common.lang.ClassUtils;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.KafkaConnector;
import consumer.kafka.MessageAndMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.*;

/**
 * 使用官方Kafka接入Spark Streaming方法
 * 使用High Consumer API
 * Created by shimn on 2016/12/1.
 */
public class KafkaNativeConnector extends KafkaConnector {

    public KafkaNativeConnector(Properties props, JavaStreamingContext jsc, Collection<String> topics) {
        super(props, jsc, topics);
    }

    @Override
    public final JavaInputDStream<ConsumerRecord<String, String>> buildInputDStream() {
        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("zookeeper.connect", props.getProperty("zookeeper.connect",
                "10.100.3.88:2181,10.100.3.89:2181,10.100.3.90:2181"));
        kafkaParams.put("bootstrap.servers", props.getProperty("bootstrap.servers",
                "10.100.3.88:9092,10.100.3.89:9092,10.100.3.90:9092"));
        kafkaParams.put("group.id", props.getProperty("group.id", "jhcomn-test-group"));
        kafkaParams.put("auto.offset.reset", props.getProperty("auto.offset.reset", "latest"));
        kafkaParams.put("enable.auto.commit", Boolean.parseBoolean(props.getProperty("enable.auto.commit", "true")));
        kafkaParams.put("auto.commit.interval.ms", props.getProperty("auto.commit.interval.ms", "1000"));
        kafkaParams.put("session.timeout.ms", props.getProperty("session.timeout.ms", "30000"));
        kafkaParams.put("key.deserializer",
                        ClassUtils.loadClass(props.getProperty("key.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer")));
        kafkaParams.put("value.deserializer",
                        ClassUtils.loadClass(props.getProperty("value.deserializer",
                        "org.apache.kafka.common.serialization.StringDeserializer")));

        return KafkaUtils.createDirectStream(
                jsc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
        );
    }

    @Override
    public final JavaDStream<MessageAndMetadata> buildReceiverDStream() {
        return null;
    }

    @Override
    public void run() {
    }
}

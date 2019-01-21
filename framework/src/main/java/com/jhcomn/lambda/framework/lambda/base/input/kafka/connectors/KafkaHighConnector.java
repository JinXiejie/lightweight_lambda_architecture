package com.jhcomn.lambda.framework.lambda.base.input.kafka.connectors;

import com.jhcomn.lambda.framework.lambda.base.input.kafka.KafkaConnector;
import consumer.kafka.MessageAndMetadata;
import consumer.kafka.ReceiverLauncher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Collection;
import java.util.Properties;

/**
 * 使用kafka-spark-consumer框架接入Spark Streaming
 * Created by shimn on 2016/12/1.
 */
public class KafkaHighConnector extends KafkaConnector {

    // Specify number of Receivers you need. default = number of partitions(Kafka)
    protected int numberOfReceivers = 3;

    public KafkaHighConnector(Properties props, JavaStreamingContext jsc, Collection<String> topics) {
        super(props, jsc, topics);
    }

    public KafkaHighConnector(Properties props, JavaStreamingContext jsc, Collection<String> topics, int numberOfReceivers) {
        this(props, jsc, topics);
        this.numberOfReceivers = numberOfReceivers;
    }

    @Override
    public final JavaDStream<MessageAndMetadata> buildReceiverDStream() {
        return ReceiverLauncher.launch(
                jsc, props, numberOfReceivers, StorageLevel.MEMORY_ONLY());
    }

    @Override
    public final JavaInputDStream<ConsumerRecord<String, String>> buildInputDStream() {
        return null;
    }

    @Override
    public void run() {

    }
}

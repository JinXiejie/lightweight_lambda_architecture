package com.jhcomn.lambda.framework.lambda.base.input.kafka;

import com.jhcomn.lambda.framework.lambda.base.input.IDataConnector;
import consumer.kafka.MessageAndMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Collection;
import java.util.Properties;

/**
 * Kafka作为数据源
 * Created by shimn on 2016/12/1.
 */
public abstract class KafkaConnector implements IDataConnector {

    protected Properties props;
    protected JavaStreamingContext jsc;
    protected Collection<String> topics;

    public KafkaConnector(Properties props, JavaStreamingContext jsc) {
        this.props = props;
        this.jsc = jsc;
    }

    public KafkaConnector(Properties props, JavaStreamingContext jsc, Collection<String> topics) {
        this.props = props;
        this.jsc = jsc;
        this.topics = topics;
    }

    public abstract JavaInputDStream<ConsumerRecord<String, String>> buildInputDStream();

    public abstract JavaDStream<MessageAndMetadata> buildReceiverDStream();

    protected abstract void run();

    @Override
    public synchronized void start() {
        run();
    }

    @Override
    public synchronized void stop() {
        jsc.stop(false);
    }
}

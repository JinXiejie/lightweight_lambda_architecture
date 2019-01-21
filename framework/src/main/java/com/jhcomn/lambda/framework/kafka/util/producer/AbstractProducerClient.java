package com.jhcomn.lambda.framework.kafka.util.producer;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ClusterProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka Producer 基类
 * Created by shimn on 2016/11/25.
 */
public abstract class AbstractProducerClient implements IProducer{

    private static final String TAG = "AbstractProducerClient";
    private static final Logger log = LoggerFactory.getLogger(AbstractProducerClient.class);

    protected Producer<String, String> producer;

    public AbstractProducerClient() {
        // create instance for properties to access producer configs
        Properties props = new Properties();
        //Assign localhost id
        props.put("bootstrap.servers", ClusterProperties.KAFKA_CLUSTER);
        //同步模式or异步模式
        props.put("producer.type", "sync");
        //Set acknowledgements for producer requests.
        props.put("acks", "-1");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);
        //Specify buffer size in config
        props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //实例化KafkaProducer
        producer = new KafkaProducer<String, String>(props);
    }

    @Override
    public void stop() {
        try {
            if (producer != null)
                producer.close();
            producer = null;
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}

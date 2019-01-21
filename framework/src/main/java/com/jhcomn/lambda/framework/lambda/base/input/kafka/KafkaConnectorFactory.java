package com.jhcomn.lambda.framework.lambda.base.input.kafka;

import com.jhcomn.lambda.framework.lambda.base.input.IDataConnectorFactory;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.connectors.KafkaHighConnector;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.connectors.KafkaNativeConnector;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Collection;
import java.util.Properties;

/**
 * Kafka数据源工厂
 * Created by shimn on 2016/12/1.
 */
public class KafkaConnectorFactory implements IDataConnectorFactory {

    /**
     * 创建原生Kafka接入Spark Streaming
     * @param props
     * @param jsc
     * @param topics
     * @return
     */
    public static KafkaNativeConnector createKafkaNativeDataConnector
            (Properties props, JavaStreamingContext jsc, Collection<String> topics) {
        return new KafkaNativeConnector(props, jsc, topics);
    }

    /**
     * 创建Kafka-Spark-Consumer接入Spark Streaming
     * @param props
     * @param jsc
     * @param topics
     * @return
     */
    public static KafkaHighConnector createKafkaHighDataConnector
            (Properties props, JavaStreamingContext jsc, Collection<String> topics) {
        return new KafkaHighConnector(props, jsc, topics);
    }

}

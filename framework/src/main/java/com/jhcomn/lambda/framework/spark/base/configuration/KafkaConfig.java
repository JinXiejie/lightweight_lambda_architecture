package com.jhcomn.lambda.framework.spark.base.configuration;

/**
 * 结合Kafka接入Streaming的Config
 * Created by shimn on 2016/11/30.
 */
public class KafkaConfig extends SparkConfig{

    public String[] topics;
    public String groupID;
    public String keyDecoderClass;
    public String messageDecoderClass;

    public KafkaConfig(String appName) {
        super(appName);
    }

    public KafkaConfig(String appName, String master) {
        super(appName, master);
    }
}

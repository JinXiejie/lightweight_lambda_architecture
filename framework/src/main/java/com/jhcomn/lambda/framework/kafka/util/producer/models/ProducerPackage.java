package com.jhcomn.lambda.framework.kafka.util.producer.models;


import com.jhcomn.lambda.packages.AbstractPackage;

/**
 * ProducerRecord<String, String> 的 wrapper
 * Created by shimn on 2016/11/25.
 */
public class ProducerPackage extends AbstractPackage {
    public String topic;
    public String key;
    public String value;

    public ProducerPackage() {
    }

    public ProducerPackage(String topic, String key, String value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }
}

package com.jhcomn.lambda.framework.kafka.util.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by shimn on 2016/12/2.
 */
public class WritableBeanDeSerializer implements Deserializer {
    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return null;
    }

    @Override
    public void close() {

    }
}

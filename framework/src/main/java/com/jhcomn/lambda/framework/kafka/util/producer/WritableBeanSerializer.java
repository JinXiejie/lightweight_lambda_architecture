package com.jhcomn.lambda.framework.kafka.util.producer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by shimn on 2016/12/2.
 */
public class WritableBeanSerializer implements Serializer {
    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        return JSON.toJSONBytes(data);
    }

    @Override
    public void close() {

    }
}

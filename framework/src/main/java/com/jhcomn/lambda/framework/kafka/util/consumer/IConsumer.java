package com.jhcomn.lambda.framework.kafka.util.consumer;

import java.util.ArrayList;

/**
 * Kafka Consumer interface
 * Created by shimn on 2016/11/25.
 */
public interface IConsumer {
    //订阅主题
    void subscribe(ArrayList<String> topics);
    //取消订阅所有主题
    void unsubscibe();
    //开始消费
    void start();
    //停止消费
    void stop();
}

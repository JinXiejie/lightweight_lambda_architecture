package com.jhcomn.lambda.framework.kafka.util.producer;


import com.jhcomn.lambda.packages.IPackage;

/**
 * Kafka Producer interface
 * Created by shimn on 2016/11/25.
 */
public interface IProducer {
    //发送消息
    void send(IPackage pkg);
    //停止生产
    void stop();
}

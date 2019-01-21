package com.jhcomn.lambda.framework.lambda.serving.result.mq;

/**
 * kafka生产者管理
 * 类似线程池的做法，首先一次性开辟3个kafka生产者
 * Created by shimn on 2017/5/10.
 */
public class ProducerManager {

    private static volatile ServingProducer producer = null;

    public static ServingProducer getProducer() {
        if (producer == null) {
            synchronized (ProducerManager.class) {
                if (producer == null) {
                    producer = new ServingProducer();
                }
            }
        }
        return producer;
    }

    public static void release() {
        if (producer != null) {
            producer.stop();
            producer = null;
        }
    }
}

package com.jhcomn.lambda.framework.lambda.serving.result.mq;

import com.jhcomn.lambda.framework.kafka.util.producer.IProducer;
import com.jhcomn.lambda.packages.IPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka producer线程池
 * refer: http://distantlight1.iteye.com/blog/2293778
 * Created by shimn on 2017/5/10.
 */
public class ServingProducer implements IProducer {

    private static final String TAG = "ServingProducer";
    private static final Logger log = LoggerFactory.getLogger(ServingProducer.class);

    private static final int NUM = 3; //kafka生产者总数
    private ProducerClient[] pool;
    private int index = 0; //轮询id

    public ServingProducer() {
        //init kafka producer pool
        pool = new ProducerClient[NUM];
        for (int i = 0; i < NUM; i++) {
            ProducerClient client = new ProducerClient();
            pool[i] = client;
        }
    }

    @Override
    public void send(IPackage pkg) {
        if (index >= Integer.MAX_VALUE)
            index = 0;
        ProducerClient producer = pool[index++ % NUM];
        if (producer != null)
            producer.send(pkg);
        else {
            System.out.println("producer of pool is null, index = " + (index % NUM) + "...");
            for (ProducerClient client : pool) {
                if (client != null)
                    client.send(pkg);
            }
        }
    }

    @Override
    public void stop() {
        for (int i = 0; i < NUM; i++) {
            ProducerClient client = pool[i];
            if (client != null) {
                client.stop();
                client = null;
            }
        }
    }
}

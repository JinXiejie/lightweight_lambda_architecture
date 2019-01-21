package com.jhcomn.lambda.framework.lambda.serving.result.mq;

import com.jhcomn.lambda.framework.kafka.util.producer.AbstractProducerClient;
import com.jhcomn.lambda.framework.kafka.util.producer.models.ProducerPackage;
import com.jhcomn.lambda.packages.IPackage;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka生产者单一客户端
 * Created by shimn on 2017/5/12.
 */
public class ProducerClient extends AbstractProducerClient {

    private static final String TAG = "ProducerClient";
    private static final Logger log = LoggerFactory.getLogger(ProducerClient.class);

    public ProducerClient() {
        super();
    }

    @Override
    public void send(IPackage pkg) {
        if (producer == null) {
            log.error("KafkaProducer未实例化");
            return;
        }
        if (pkg == null) {
            log.error("无效数据包");
            return;
        }

        ProducerPackage data = (ProducerPackage) pkg;
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(data.topic, data.key, data.value);
            producer.send(record, new SendCallback(record, 0));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    class SendCallback implements Callback {
        ProducerRecord<String, String> record;
        int sendSeq = 0;

        public SendCallback(ProducerRecord record, int sendSeq) {
            this.record = record;
            this.sendSeq = sendSeq;
        }
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            //send success
            if (null == e) {
                String meta = "topic:" + recordMetadata.topic() + ", partition:"
                        + recordMetadata.topic() + ", offset:" + recordMetadata.offset();
                log.info("send message success, record:" + record.toString() + ", meta:" + meta);
                return;
            }
            //send failed
            log.error("send message failed, seq:" + sendSeq + ", record:" + record.toString() + ", errmsg:" + e.getMessage());
            if (sendSeq < 1) {
                //有且仅重发一次
                producer.send(record, new SendCallback(record, ++sendSeq));
            }
        }
    }
}

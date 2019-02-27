package com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by jinxiejie on 2017/5/17.
 */
public class KafkaKeySender {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaKeySender.class);
    private static final String ACKS_VALUE = "all";//"all", "-1"(Default), "0", "1";
    private static final String Localhost_Id = "10.100.3.88:9092;10.100.3.89:9092;10.100.3.90:9092";
    private static final long LingerTime = 1;
    private static final String topic = "INPUT";

    private static Properties props = new Properties();
    private static volatile KafkaKeySender sender = null;
    private KafkaKeySender(){
        props.put("bootstrap.servers", Localhost_Id);
        props.put("acks",ACKS_VALUE);
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", LingerTime);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    //double-checked
    public static KafkaKeySender getInstance() {
        if (sender == null) {
            synchronized (KafkaKeySender.class) {
                if (sender == null) {
                    sender = new KafkaKeySender();
                }
            }
        }
        return sender;
    }

    public KafkaKeySender Send( String key, String jsonStr){
        // TODO Auto-generated method stub
//        String jsonStr = topic + "is trained successfully and the model could utilize";
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, jsonStr);
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
                // TODO Auto-generated method stub
                if (e != null) {
                    LOG.error("the producer has a error:" + e.getMessage());
                    System.out.println("the producer has a error:" + e.getMessage());
                }
                else {
                    LOG.info("The  offset of the record we just sent is: " + metadata.offset());
                    LOG.info("The partition of the record we just sent is: " + metadata.partition());
                    System.out.println("The offset of the record we just sent is: " + metadata.offset());
                    System.out.println("The partition of the record we just sent is: " + metadata.partition());
                }
            }
        });
        try {
            Thread.sleep(1000);
            producer.close();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    public void release() {
        sender = null;
    }
}

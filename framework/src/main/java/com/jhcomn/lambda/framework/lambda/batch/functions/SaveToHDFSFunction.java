package com.jhcomn.lambda.framework.lambda.batch.functions;

import consumer.kafka.MessageAndMetadata;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 保存原始数据的函数
 * Created by shimn on 2016/12/20.
 */
final class SaveToHDFSFunction implements VoidFunction2<JavaRDD<MessageAndMetadata>, Time> {

    private static final Logger log = LoggerFactory.getLogger(SaveToHDFSFunction.class);

//    private final String prefix;
//    private final String suffix;


    @Override
    public void call(JavaRDD<MessageAndMetadata> rdd, Time time) throws Exception {

    }
}

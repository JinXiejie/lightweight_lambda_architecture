package com.jhcomn.lambda.framework.lambda.batch.functions;

import consumer.kafka.MessageAndMetadata;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Framework for executing the batch layer update, and storing data to persistent storage,
 * in the context of a streaming framework.
 *
 * Created by shimn on 2016/12/18.
 */
final class BatchUpdateFunction implements VoidFunction2<JavaRDD<MessageAndMetadata>, Time> {

    private static final Logger log = LoggerFactory.getLogger(BatchUpdateFunction.class);



    @Override
    public void call(JavaRDD<MessageAndMetadata> v1, Time v2) throws Exception {

    }
}

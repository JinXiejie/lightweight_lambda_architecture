package com.jhcomn.lambda.framework.kafka.util;

import scala.actors.threadpool.Arrays;

import java.util.Collection;

/**
 * Kafka Topics
 * Created by shimn on 2016/11/29.
 */
public class TopicsManager {

//    private static Collection<String> topics = null;
    private static Collection<String> topics = Arrays.asList(new String[]{"test"});

    public static Collection<String> getTopics() {
        return topics;
    }

    public static void setTopics(Collection<String> topics) {
        TopicsManager.topics = topics;
    }
}

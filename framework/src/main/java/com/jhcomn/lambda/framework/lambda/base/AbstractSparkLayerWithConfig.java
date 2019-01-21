package com.jhcomn.lambda.framework.lambda.base;

import com.google.common.base.Preconditions;
import com.jhcomn.lambda.framework.common.lang.ClassUtils;
import com.jhcomn.lambda.framework.common.settings.ConfigUtils;
import com.jhcomn.lambda.framework.kafka.util.TopicsManager;
import com.typesafe.config.Config;
import kafka.serializer.Decoder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by shimn on 2016/11/25.
 */
public abstract class AbstractSparkLayerWithConfig<K, M> implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(AbstractSparkLayerWithConfig.class);

    private Config config;
    private String id;
    private String streamingMaster;
    private String inputTopic;
    private String inputTopicLockMaster;
    private String inputBroker;
    private String updateTopic;
    private String updateTopicLockMaster;
    private Class<K> keyClass;
    private Class<M> messageClass;
    private Class<? extends Decoder<K>> keyDecoderClass;
    private Class<? extends Decoder<M>> messageDecoderClass;
    private int generationIntervalSec;
    private Map<String,Object> extraSparkConfig;

    /**
     * @return layer-specific config grouping under "oryx", like "com.jhcomn.lambda.app.batch" or "speed"
     */
    protected abstract String getConfigGroup();

    /**
     * @return display name for layer like "BatchLayer"
     */
    protected abstract String getLayerName();

    protected final Config getConfig() {
        return config;
    }

    protected final String getID() {
        return id;
    }

    protected final String getGroupID() {
        return "JhcomnGroup-" + getLayerName() + "-" + getID();
    }

    protected final String getInputTopicLockMaster() {
        return inputTopicLockMaster;
    }

    protected final Class<K> getKeyClass() {
        return keyClass;
    }

    protected final Class<M> getMessageClass() {
        return messageClass;
    }

    protected AbstractSparkLayerWithConfig(Config config) {
        Objects.requireNonNull(config);
        log.info("Configuration:\n{}", ConfigUtils.prettyPrint(config));

        String group = getConfigGroup();
        this.config = config;
        String configuredID = ConfigUtils.getOptionalString(config, "jhcomn.id");
        this.id = configuredID == null ? UUID.randomUUID().toString() : configuredID;
        this.streamingMaster = config.getString("jhcomn." + group + ".streaming.master");
        this.inputTopic = config.getString("jhcomn.input-topic.message.topic");
        this.inputTopicLockMaster = config.getString("jhcomn.input-topic.lock.master");
        this.inputBroker = config.getString("jhcomn.input-topic.broker");
        this.updateTopic = ConfigUtils.getOptionalString(config, "jhcomn.update-topic.message.topic");
        this.updateTopicLockMaster = ConfigUtils.getOptionalString(config, "jhcomn.update-topic.lock.master");
        this.keyClass = ClassUtils.loadClass(config.getString("jhcomn.input-topic.message.key-class"));
        this.messageClass =
                ClassUtils.loadClass(config.getString("jhcomn.input-topic.message.message-class"));
        this.keyDecoderClass = (Class<? extends Decoder<K>>) ClassUtils.loadClass(
                config.getString("jhcomn.input-topic.message.key-decoder-class"), Decoder.class);
        this.messageDecoderClass = (Class<? extends Decoder<M>>) ClassUtils.loadClass(
                config.getString("jhcomn.input-topic.message.message-decoder-class"), Decoder.class);
        this.generationIntervalSec = config.getInt("jhcomn." + group + ".streaming.generation-interval-sec");

        this.extraSparkConfig = new HashMap<>();
        config.getConfig("jhcomn." + group + ".streaming.config").entrySet().forEach(e ->
                extraSparkConfig.put(e.getKey(), e.getValue().unwrapped())
        );

        Preconditions.checkArgument(generationIntervalSec > 0);
    }

    protected final JavaStreamingContext buildStreamingContext() {
        log.info("Starting SparkContext with interval {} seconds", generationIntervalSec);

        SparkConf sparkConf = new SparkConf();

        // Only for tests, really
        if (sparkConf.getOption("spark.master").isEmpty()) {
            log.info("Overriding master to {} for tests", streamingMaster);
            sparkConf.setMaster(streamingMaster);
        }
        // Only for tests, really
        if (sparkConf.getOption("spark.app.name").isEmpty()) {
            String appName = "JHcomn" + getLayerName();
            if (id != null) {
                appName = appName + "-" + id;
            }
            log.info("Overriding app name to {} for tests", appName);
            sparkConf.setAppName(appName);
        }
        extraSparkConfig.forEach((key, value) -> sparkConf.setIfMissing(key, value.toString()));

        // Turn this down to prevent long blocking at shutdown
        sparkConf.setIfMissing(
                "spark.streaming.gracefulStopTimeout",
                Long.toString(TimeUnit.MILLISECONDS.convert(generationIntervalSec, TimeUnit.SECONDS)));
        sparkConf.setIfMissing("spark.cleaner.ttl", Integer.toString(20 * generationIntervalSec));
        long generationIntervalMS =
                TimeUnit.MILLISECONDS.convert(generationIntervalSec, TimeUnit.SECONDS);

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf));
        return new JavaStreamingContext(jsc, new Duration(generationIntervalMS));
    }

    protected final JavaInputDStream<ConsumerRecord<String, String>> buildInputDStream(
            JavaStreamingContext streamingContext) {

        Map<String,Object> kafkaParams = new HashMap<>();
        kafkaParams.put("zookeeper.connect", inputTopicLockMaster); // needed for SimpleConsumer later
        String groupID = getGroupID();
        kafkaParams.put("group.id", groupID);
        // Don't re-consume old messages from input by default
        kafkaParams.put("auto.offset.reset", "latest"); // becomes "latest" in Kafka 0.9+
        kafkaParams.put("enable.auto.commit", "true");
        kafkaParams.put("bootstrap.servers", inputBroker);
        kafkaParams.put("auto.commit.interval.ms", TimeUnit.MILLISECONDS.convert(generationIntervalSec, TimeUnit.SECONDS));
        kafkaParams.put("session.timeout.ms", "30000");
        kafkaParams.put("key.deserializer", keyDecoderClass);
        kafkaParams.put("value.deserializer", messageDecoderClass);

        return KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(TopicsManager.getTopics(), kafkaParams));
    }
}

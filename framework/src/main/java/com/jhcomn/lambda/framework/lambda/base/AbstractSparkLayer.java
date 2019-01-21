package com.jhcomn.lambda.framework.lambda.base;

import com.jhcomn.lambda.framework.lambda.base.common.constants.*;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.KafkaConnector;
import com.jhcomn.lambda.framework.lambda.base.utils.monitor.JMXUpdateMonitor;
import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.framework.lambda.sync.tag.TagSyncController;
import com.jhcomn.lambda.framework.spark.base.SparkBaseManager;
import com.jhcomn.lambda.framework.spark.base.configuration.IConfig;
import com.jhcomn.lambda.framework.spark.base.configuration.SparkConfig;
import com.jhcomn.lambda.packages.common.ApplicationDatas;
import com.jhcomn.lambda.packages.tag.Tag;
import com.jhcomn.lambda.packages.tag.TagListCache;
import consumer.kafka.MessageAndMetadata;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.Properties;

/**
 * lambda抽象层
 * Created by shimn on 2016/11/25.
 */
public abstract class AbstractSparkLayer implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(AbstractSparkLayer.class);

    static {
        //TODO 待改善，更新tag缓存
        ISyncController controller = new TagSyncController();
        if (ApplicationDatas.tagListCacheMap == null)
            ApplicationDatas.tagListCacheMap = new HashMap<>();
        ApplicationDatas.tagListCacheMap.put(ConstantDatas.HFCT,
                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.HFCT)));
        ApplicationDatas.tagListCacheMap.put(ConstantDatas.UW,
                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.UW)));
        ApplicationDatas.tagListCacheMap.put(ConstantDatas.TEV,
                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.TEV)));
        ApplicationDatas.tagListCacheMap.put(ConstantDatas.INFRARED,
                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.INFRARED)));
        ApplicationDatas.tagListCacheMap.put(ConstantDatas.UHF,
                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.UHF)));
        System.out.println("tagMap's size = " + ApplicationDatas.tagListCacheMap.size() +
                "\t map has HFCT ? = " + ApplicationDatas.tagListCacheMap.containsKey(ConstantDatas.HFCT));
        System.out.println(ApplicationDatas.tagListCacheMap.get(ConstantDatas.HFCT).getTagList().size());
    }

    private IConfig config;
    protected String id;
    protected Collection<String> topics;
    protected Properties properties;

    protected String uid;
    protected Collection<String> mlTopics;

    protected SparkSession spark;   //spark entry
    protected JavaStreamingContext jsc;
    protected KafkaConnector dataConnector;
    protected JavaDStream<MessageAndMetadata> kafkaStream;

    //JVM内存监控
    protected JMXUpdateMonitor jmxMonitor = null;

    private Class<?> keyClass;
    private Class<?> valueClass;

    protected final Class<?> getKeyClass() {
        return keyClass;
    }

    protected final Class<?> getValueClass() {
        return valueClass;
    }
    /**
     * @return layer-specific config grouping under "oryx", like "com.jhcomn.lambda.app.batch" or "speed"
     */
    protected abstract String getConfigGroup();

    /**
     * @return display name for layer like "BatchLayer"
     */
    protected abstract String getLayerName();

    protected final IConfig getConfig() {
        return config;
    }

    protected final String getID() {
        return id;
    }

    protected final String getGroupID() {
        return "JHcomnGroup-" + getLayerName() + "-" + getID();
    }

    //        maps.put("spark.executor.memory", "2g");    //每个executor分配2g内存
//        maps.put("spark.executor.cores", "3");
    protected AbstractSparkLayer(Properties props) {
        this.id = UUID.randomUUID().toString();
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("spark.cores.max", "12");  //spark每个application最多占用12个cpu核(3个节点总共24个核)，默认是每个application占用全部
        maps.put("spark.streaming.kafka.maxRatePerPartition", "10000"); //限制每秒从topic的每个partition最多消费的消息条数
        //动态分配executor个数
        //https://spark.apache.org/docs/latest/job-scheduling.html#dynamic-resource-allocation
        //动态分配可以使的 Spark 的应用在有后续积压的在等待的 task 时请求 executor，并且在空闲时释放这些 executor
//        maps.put("spark.dynamicAllocation.enabled", "true");
//        maps.put("spark.shuffle.service.enabled", "true");
        this.config = new SparkConfig(getGroupID(), "spark://Master:7077", 1000, maps);

        //创建Spark Configuration
        SparkBaseManager.buildSparkConf(this.config);

        if (props != null) {
            this.properties = props;
            this.uid = props.getProperty("uid", "1");
            this.topics = Arrays.asList(props.getProperty("kafka.topic").split(","));
//            System.out.println(this.topics.toString() + "-size:" + this.topics.size());
            this.mlTopics = Arrays.asList(props.getProperty("model.topic", "TEST").split(","));
            //获取批处理任务启动时间
            com.jhcomn.lambda.framework.lambda.base.common.constants.Properties.BATCH_WORK_TIME
                    = props.getProperty("batch.worktime", "00:00:00");
        }

//        this.keyClass = (Class<K>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        this.valueClass = (Class<V>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    protected final SparkSession buildSparkSession() throws Exception {
        return SparkBaseManager.buildSparkSession();
    }

    protected final JavaStreamingContext buildStreamingContext() throws Exception {
//        JavaSparkContext javaSparkContext = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf));
//        JavaSparkContext javaSparkContext = JavaSparkContext.fromSparkContext(spark.sparkContext());
        return SparkBaseManager.buildStreamingContext();
    }

    protected abstract void buildDataConnector();

    public synchronized void start() {
        if (id != null) {
            log.info("Starting {}", getLayerName() + id);
        }
        try {

            //启动JVM监控
            jmxMonitor = JMXUpdateMonitor.getInstance();
            jmxMonitor.start();

            /*
             * initial
             */
            spark = buildSparkSession();

            if (getLayerName().equals(ConstantDatas.SPEED_LAYER_NAME)) {
                jsc = buildStreamingContext();
                buildDataConnector();
                /*
                 * integrate kafka to spark streaming
                 */
                //        kafkaStream = dataConnector.buildInputDStream();

                /*
                 * kafka-spark-consumer integrate to spark-streaming
                 */
                kafkaStream = dataConnector.buildReceiverDStream();
            }

            //根据层次修改操作逻辑
            run();

            //start streaming
            if (getLayerName().equals(ConstantDatas.SPEED_LAYER_NAME) && jsc != null)
                jsc.start();

        } catch (Exception e) {
            log.error("SparkSession or StreamingContext创建失败");
            e.printStackTrace();
        } finally {
        }
    }

    public abstract void run();

    public void await() throws InterruptedException {
        JavaStreamingContext streamingContext;
        synchronized (this) {
            streamingContext = jsc;
        }
        log.info("Spark Streaming is running");
        streamingContext.awaitTermination();
    }

    public synchronized void close() throws IOException {
        //停止JVM监控
        jmxMonitor.stop();
        //释放spark资源
        if (jsc != null) {
            log.info("Shutting down Spark Streaming; this may take some time");
            jsc.stop(true, false);
            jsc = null;
            spark.stop();
            spark = null;
        }
    }

}

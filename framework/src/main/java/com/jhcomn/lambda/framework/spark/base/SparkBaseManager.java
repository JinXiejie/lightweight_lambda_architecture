package com.jhcomn.lambda.framework.spark.base;

import com.jhcomn.lambda.framework.spark.base.configuration.IConfig;
import com.jhcomn.lambda.framework.spark.base.configuration.SparkConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Spark入口管理
 * 单例DCL改进DCL不安全（可能存在重排序,加上volatile禁止重排序）：http://ifeve.com/from-singleton-happens-before/
 * Created by shimn on 2016/11/29.
 */
public final class SparkBaseManager {

    private static final String APP_NAME = "TEST";
    private static final Logger log = LoggerFactory.getLogger(SparkBaseManager.class);

//    private static final SparkConf conf = new SparkConf()
//                                        .setAppName(APP_NAME)
//                                        .setMaster("spark://Master:7077")
//                                        .set("spark.rpc.netty.dispatcher.numThreads", "2");

    /**
     * 单例创建SparkConf
     */
    private static volatile SparkConf conf = null;

    public static SparkConf buildSparkConf(IConfig config) {
        if (conf == null && config != null) {
            synchronized (SparkBaseManager.class) {
                if (conf == null) {
                    SparkConfig sparkConfig = (SparkConfig) config;
                    conf = new SparkConf().setAppName(sparkConfig.appName).setMaster(sparkConfig.master);
                    if (sparkConfig.sparkConfMap != null)
                        for (Map.Entry<String, String> entry : sparkConfig.sparkConfMap.entrySet())
                            conf.set(entry.getKey(), entry.getValue());
                }
            }
        }
        return conf;
    }

    /**
     * 单例获取SparkContext和SparkSession
     */
    //SparkContext
    private static volatile SparkContext sc = null;

    //SparkSession
    private static volatile SparkSession spark = null;

    public static SparkContext buildSparkContext() {
        if (sc == null && conf != null) {
            synchronized (SparkBaseManager.class) {
                if (sc == null) {
                    sc = new SparkContext(conf);
                }
            }
        }
        return sc;
    }

    /**
     * 停止SparkContext
     */
    public static void stopSparkContext() {
        try {
            if (sc != null)
                sc.stop();
            sc = null;
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public static SparkSession buildSparkSession() {
        if (spark == null && conf != null) {
            synchronized (SparkBaseManager.class) {
                if (spark == null) {
                    spark = SparkSession.builder().config(conf).getOrCreate();
                }
            }
        }
        return spark;
    }

    /**
     * 停止SparkSession
     */
    public static void stopSparkSession() {
        try {
            if (spark != null)
                spark.stop();
            spark = null;
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    private static volatile JavaSparkContext jsc = null;
    public static JavaSparkContext buildJavaSparkContext() {
        if (jsc == null) {
            synchronized (SparkBaseManager.class) {
                if (jsc == null && spark != null) {
                    jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
                }
            }
        }
        return jsc;
    }

    /**
     * 单例获取StreamingContext
     */
    private static volatile JavaStreamingContext jssc = null;

    public static JavaStreamingContext buildStreamingContext() {
        if (jssc == null || jsc == null) {
            synchronized (SparkBaseManager.class) {
                if (jsc == null && spark != null) {
                    jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
                }
                if (jssc == null && jsc != null) {
                    jssc = new JavaStreamingContext(jsc, Duration.apply(1000));
                }
            }
        }
        return jssc;
    }

    /**
     * 停止SparkSession
     * @param isStopSparkContext 是否停止对应的SparkContext
     */
    public static void stopSparkStreaming(boolean isStopSparkContext) {
        try {
            if (jssc != null)
                jssc.stop(isStopSparkContext);
            jssc = null;
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 单例获取Hadoop Configuration
     */
    private static Configuration hadoopConf = null;

    public static Configuration buildHadoopConfiguration() {
        if (hadoopConf == null) {
            synchronized (SparkBaseManager.class) {
                if (jsc == null)
                    jsc = buildJavaSparkContext();
                if (hadoopConf == null && jsc != null) {
                    hadoopConf = jsc.hadoopConfiguration();
                }
            }
        }
        return hadoopConf;
    }

    /**
     * 停止Spark
     */
    public static void stopAll() {
        stopSparkSession();
        stopSparkStreaming(false);
        stopSparkContext();
    }

}

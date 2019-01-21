package com.jhcomn.lambda.framework.spark.base.configuration;

import java.util.Map;

/**
 * SparkConf配置项
 * Created by shimn on 2016/11/29.
 */
public class SparkConfig extends AbstractConfig {

    //Spark App Name
    public String appName = "TEST";

    //Spark Master: "spark://Master:7077"(standalone),"yarn"
    public String master = "spark://Master:7077";

    public int streamingInterval = 1000;

    //other configs
    public Map<String, String> sparkConfMap;

    public SparkConfig(String appName) {
        this.appName = appName;
    }

    public SparkConfig(String appName, String master) {
        this.appName = appName;
        this.master = master;
    }

    public SparkConfig(String appName, String master, int streamingInterval) {
        this.appName = appName;
        this.master = master;
        this.streamingInterval = streamingInterval;
    }

    public SparkConfig(String appName, String master, int streamingInterval, Map<String, String> sparkConfMap) {
        this.appName = appName;
        this.master = master;
        this.streamingInterval = streamingInterval;
        this.sparkConfMap = sparkConfMap;
    }
}

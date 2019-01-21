package com.jhcomn.lambda.framework.lambda.base.common.constants;

/**
 * Created by shimn on 2016/11/25.
 */
public class ClusterProperties {

    /**
     * 集群节点ip
     */
    public static final String MASTER = "10.100.3.88";
    public static final String SLAVE1 = "10.100.3.89";
    public static final String SLAVE2 = "10.100.3.90";

    /**
     * Kafka broker 端口
     */
    public static final String KAFKA_BROKER_PORT = "9092";
    public static final String KAFKA_CLUSTER = MASTER + ":" + KAFKA_BROKER_PORT + ", "
                                                + SLAVE1 + ":" + KAFKA_BROKER_PORT + ", "
                                                + SLAVE2 + ":" + KAFKA_BROKER_PORT;

    /**
     * Zookeeper 集群配置
     */
    public static final String ZK_PORT = "2181";
    public static final String ZK_QUORUM = MASTER + "," + SLAVE1 + "," + SLAVE2;

    /**
     * HBase 集群配置
     */
    public static final String HBASE_MASTER = "hdfs://" + MASTER + ":60000";
    public static final String HBASE_ROOT_DIR = "hdfs://" + MASTER + ":9000/hbase";
}

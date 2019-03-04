package com.jhcomn.lambda.framework.lambda.speed;

import com.jhcomn.lambda.framework.lambda.base.AbstractSparkLayer;
import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.KafkaConnectorFactory;
import com.jhcomn.lambda.framework.lambda.batch.functions.SaveToSQLFunction;
import com.jhcomn.lambda.framework.lambda.pkg.meta.RemoteDataTable;
import com.jhcomn.lambda.framework.lambda.pkg.meta.TagListDataTable;
import com.jhcomn.lambda.framework.lambda.pkg.meta.TestDataTable;
import com.jhcomn.lambda.framework.spark.base.SparkBaseManager;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseTableDao;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.impl.HBaseTableDaoImpl;
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFAnalyze;
import consumer.kafka.ProcessedOffsetManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 流计算层 --> 增量计算
 * Created by shimn on 2016/11/30.
 */
public class SpeedLayer extends AbstractSparkLayer {

    private static final Logger log = LoggerFactory.getLogger(SpeedLayer.class);

    /**
     * 初始化HBase
     */
    static {
        HBaseTableDao tableDao = new HBaseTableDaoImpl();
        tableDao.init();
        try {
            //HBASE remote_data table init
            if (!tableDao.isTableOK(RemoteDataTable.tableName)) {
                tableDao.createTable(RemoteDataTable.tableName, RemoteDataTable.columnFamilys);
                tableDao.enableTable(RemoteDataTable.tableName);
            }
            //HBASE test_data table init
            if (!tableDao.isTableOK(TestDataTable.tableName)) {
                tableDao.createTable(TestDataTable.tableName, TestDataTable.columnFamilys);
                tableDao.enableTable(TestDataTable.tableName);
            }
            //HBASE tag_list table init
//            if (!tableDao.isTableOK(TagListDataTable.tableName)) {
//                tableDao.createTable(TagListDataTable.tableName, TagListDataTable.columnFamilys);
//                tableDao.enableTable(TagListDataTable.tableName);
//            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public SpeedLayer(Properties props) {
        super(props);
    }

    @Override
    protected String getConfigGroup() {
        return "com.jhcomn.lambda.app.speed";
    }

    @Override
    protected String getLayerName() {
        return ConstantDatas.SPEED_LAYER_NAME;
    }

    @Override
    protected void buildDataConnector() {
        dataConnector = KafkaConnectorFactory.createKafkaHighDataConnector(properties, jsc, this.topics);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        /**
         * kafka-spark-consumer integrate test
         */
        JavaPairDStream<Integer, Iterable<Long>> partitionOffset = ProcessedOffsetManager
                .getPartitionOffset(kafkaStream);

        Configuration hadoopConf = SparkBaseManager.buildHadoopConfiguration();

        System.out.println("-------------------UHF分割线-------------------");
        String topic = "UHF";
        UHFAnalyze uhfAnalyze = new UHFAnalyze(topic, "train");
        uhfAnalyze.receive();
//        String jsonStr = uhfAnalyze.jsonStr;
//        uhfAnalyze.uhfTest(jsonStr);
        System.out.println("-------------------UHF分割线-------------------");

        System.out.println("kafka stream saveOrUpdate to HBase");
        //application logic begin
        kafkaStream.foreachRDD(new SaveToSQLFunction(hadoopConf));
        //application logic end

        ProcessedOffsetManager.persists(partitionOffset, properties);
    }

}

package com.jhcomn.lambda.framework.lambda.batch;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.utils.ScheduledTaskManager;
import com.jhcomn.lambda.framework.lambda.base.AbstractSparkLayer;
import com.jhcomn.lambda.framework.lambda.base.input.kafka.KafkaConnectorFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.function.PairFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Properties;

/**
 * 批处理层 --> 全量计算
 * Created by shimn on 2016/12/2.
 */
public class BatchLayer extends AbstractSparkLayer {

    private static final Logger log = LoggerFactory.getLogger(BatchLayer.class);

    /**
     * 初始化HBase
     */
    /*static {
        HBaseTableDao tableDao = new HBaseTableDaoImpl();
        tableDao.init();
        try {
            if (!tableDao.isTableOK(RemoteDataTable.tableName)) {
                tableDao.createTable(RemoteDataTable.tableName, RemoteDataTable.columnFamilys);
                tableDao.enableTable(RemoteDataTable.tableName);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }*/

//    private TimerManager timerManager = null;
    private ScheduledTaskManager scheduledTaskManager = null;

    public BatchLayer(Properties props) {
        super(props);
    }

    @Override
    protected String getConfigGroup() {
        return "com.jhcomn.lambda.app.batch";
    }

    @Override
    protected String getLayerName() {
        return ConstantDatas.BATCH_LAYER_NAME;
    }

    @Override
    protected void buildDataConnector() {
//        dataConnector = KafkaConnectorFactory.createKafkaNativeDataConnector(properties, jsc, this.topics);
        dataConnector = KafkaConnectorFactory.createKafkaHighDataConnector(properties, jsc, this.topics);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        //native integrate test
//        JavaPairDStream<String, String> pairDStream = kafkaStream.mapToPair(func);
//        pairDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {
//            @Override
//            public void call(JavaPairRDD<String, String> stringStringJavaPairRDD) throws Exception {
//                List<Tuple2<String, String>> lists = stringStringJavaPairRDD.collect();
//                System.out.println("rdd size = " + lists.size());
//                for (Tuple2<String, String> record : lists) {
//                    System.out.println("key = " + record._1 + ", value = " + record._2);
//                }
//            }
//        });

        /**
         * kafka-spark-consumer integrate test
         */
//        JavaPairDStream<Integer, Iterable<Long>> partitionOffset = ProcessedOffsetManager
//                .getPartitionOffset(kafkaStream);
//
//        Configuration hadoopConf = SparkBaseManager.buildHadoopConfiguration();
//
//        //application logic begin
//        kafkaStream.foreachRDD(new SaveToSQLFunction(hadoopConf));
//        //application logic end
//
//        ProcessedOffsetManager.persists(partitionOffset, properties);

        /**
         * 启动定时器 --> 开始定时批处理数据
         */
//        timerManager = TimerManager.getInstance();
//        timerManager.start(uid, mlTopics);

        scheduledTaskManager = ScheduledTaskManager.getInstance();
        scheduledTaskManager.execute(uid, mlTopics);


//        timerManager.stop();
    }

    private static PairFunction<ConsumerRecord<String, String>, String, String>
            func =
            new PairFunction<ConsumerRecord<String, String>, String, String>() {
                @Override
                public Tuple2<String, String> call(ConsumerRecord<String, String> record) throws Exception {
                    return new Tuple2<String, String>(record.key(), record.value());
                }
            };
}

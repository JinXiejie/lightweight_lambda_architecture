package com.jhcomn.lambda.framework.lambda.batch.functions;

//import com.alibaba.fastjson.JSONObject;
import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.common.observer.IObservable;
import com.jhcomn.lambda.framework.lambda.base.common.observer.IObserver;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hbase.HBaseUpdateObservable;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.HdfsObserver;
import com.jhcomn.lambda.framework.lambda.pkg.cache.taglist.HdfsFetchTagListPkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTPkg;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsData;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsRawData;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsTypeData;
import com.jhcomn.lambda.framework.lambda.pkg.training.TrainingRawData;
import com.jhcomn.lambda.framework.lambda.pkg.training.TrainingData;
import com.jhcomn.lambda.framework.lambda.pkg.meta.RemoteDataTable;
import com.jhcomn.lambda.framework.lambda.pkg.meta.TestDataTable;
import com.jhcomn.lambda.framework.lambda.sync.tag.dispatch.ITagSyncDispatcher;
import com.jhcomn.lambda.framework.lambda.sync.tag.dispatch.impl.TagSyncDispatcher;
import com.jhcomn.lambda.framework.utils.StringUtil;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseRowDao;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.impl.HBaseRowDaoImpl;
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFAnalyze;
import com.jhcomn.lambda.packages.IPackage;
import consumer.kafka.MessageAndMetadata;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * kafka数据源转存于
 * 1)HBase表中remote_data，test_data
 * 2)MySQL表中taglist
 * Created by shimn on 2016/12/23.
 */
public class SaveToSQLFunction implements VoidFunction2<JavaRDD<MessageAndMetadata>, Time> {

    private static final Logger log = LoggerFactory.getLogger(SaveToSQLFunction.class);

    private HBaseRowDao rowDao = null;

    private IObservable observable = null;

    private Configuration hadoopConf = null;

    //tag dispatcher
    private ITagSyncDispatcher tagSyncDispatcher = null;

    public SaveToSQLFunction(Configuration hadoopConf) {
        try {
            this.hadoopConf = hadoopConf;
            //dao operations
            rowDao = new HBaseRowDaoImpl();
            rowDao.init();
            //subscribe observer of HDFS
            observable = new HBaseUpdateObservable();
            IObserver HObserver = null;
            try {
                HObserver = new HdfsObserver(FileSystem.get(hadoopConf));
                observable.attach(HObserver);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            tagSyncDispatcher = new TagSyncDispatcher();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void call(JavaRDD<MessageAndMetadata> rdd, Time time) throws Exception {
        if (rdd != null && !rdd.isEmpty()) {
            String date = StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
            //hdfs
            IPackage fetchTrainPkg = new HdfsFetchTTPkg(ConstantDatas.TRAINING, date);
            IPackage fetchTestPkg = new HdfsFetchTTPkg(ConstantDatas.TESTING, date);
            IPackage fetchTagListPkg = new HdfsFetchTagListPkg(ConstantDatas.TAGS, date);

            List<MessageAndMetadata> rddList = rdd.collect(); //collect可能会影响性能
//            System.out.println("-------------------UHF分割线-------------------");
//            String topic = "UHF";
//            UHFAnalyze uhfAnalyze = new UHFAnalyze(topic, "train");
//            uhfAnalyze.receive();
//            String jsonStr = uhfAnalyze.jsonStr;
//            uhfAnalyze.uhfTest(jsonStr);
//            System.out.println("-------------------UHF分割线-------------------");
            System.out.println(" Number of records in this batch " + rddList.size());
            for (MessageAndMetadata metadata : rddList) {
                System.out.println("model key = " + new String(metadata.getKey(), "UTF-8") + ", value = " + new String(metadata.getPayload(), "UTF-8"));

                //TODO 依据MessageAndMetadata中的key不同区分相同topic下的场景1（训练）&&场景2（测试）
                String metaKey = new String(metadata.getKey(), "UTF-8");
                if (metaKey != null && !metaKey.equals("")) {
                    String[] keys = metaKey.split("-");
                    if (keys == null || keys.length <= 0) {
                        System.out.println("kafka发送的消息key值不合法，不做任何处理");
                        return;
                    }
                    String key = keys[0];
                    String type = null;
                    if (keys.length > 1)
                        type = keys[1];
                    if (key.equals(ConstantDatas.TRAINING)) {
                        //训练集数据
                        System.out.println("接收到训练集数据");
                        ((HdfsFetchTTPkg)fetchTrainPkg).setType(type);
                        train(fetchTrainPkg, date, metadata);
                    }
                    else if (key.equals(ConstantDatas.TESTING)) {
                        //测试集数据
                        System.out.println("接收到测试集数据");
                        ((HdfsFetchTTPkg)fetchTestPkg).setType(type);
                        test(fetchTestPkg, date, metadata);
                    }
                    else if (key.equals(ConstantDatas.TAGS)) {
                        //标签数据集<id,string,type>
                        System.out.println("接收到标签数据集更新");
                        //((HdfsFetchTagListPkg)fetchTagListPkg).setType(type); //taglist的包的type需要解析才知道type
                        tagList(fetchTagListPkg, date, metadata);
                    }
                }
            }

            //update observer
            if (((HdfsFetchTTPkg)fetchTrainPkg).getMetaPkgs().size() > 0)
                observable.notify(fetchTrainPkg);
            if (((HdfsFetchTTPkg)fetchTestPkg).getMetaPkgs().size() > 0)
                observable.notify(fetchTestPkg);
            //TODO 通知taglist更新
        }
    }

    /**
     * 训练集数据预处理
     * @param fetchPkg
     * @param date
     * @param metadata
     * @throws IOException
     */
    private void train (IPackage fetchPkg, String date, MessageAndMetadata metadata) throws IOException {
        //解析MessageAndMetadata
        TrainingRawData rawData = null;
//        TrainingRawData rawData = JSONObject.parseObject(metadata.getPayload(), TrainingRawData.class);
        String id = rawData.getId(); //表单id，全局唯一
        String uid = rawData.getClientId(); //user id
        String type = rawData.getType(); //data type
        String url = rawData.getUrl(); //data dir url

        //rowkey设计需考虑“过热”问题
        String rowKey = id + "_" + date + "_" + uid;

        //hdfs
        HdfsFetchTTMetaPkg metaPkg = new HdfsFetchTTMetaPkg(uid, type, url);
        //每个任务包都有一个唯一的id
        metaPkg.setId(id);

        //step1：去重
        //TODO 遍历表中所有记录，查找以该id为前缀的记录，性能待优化
        List<Result> querys = rowDao.queryByKeyPrefix(RemoteDataTable.tableName, id + "");
        if (querys.size() > 0) {
            System.out.println("id=" + id + "的表单在RemoteDataTable中已存在！此处不做处理返回！");
            return;
        }

        //step2：如果不重复，则插入HBase的remote_data表中
        //list of trainingdata
        List<TrainingData> trainingDatas = rawData.getDatas();
        if (trainingDatas != null) {
            List<Put> puts = new ArrayList<>();
            puts.add(getPut(rowKey, RemoteDataTable.infoFamily, RemoteDataTable.idColumn, id));
            puts.add(getPut(rowKey, RemoteDataTable.infoFamily, RemoteDataTable.typeColumn, type));
            puts.add(getPut(rowKey, RemoteDataTable.infoFamily, RemoteDataTable.urlColumn, url));
            puts.add(getPut(rowKey, RemoteDataTable.infoFamily, RemoteDataTable.clientIdColumn, uid + ""));
            puts.add(getPut(rowKey, RemoteDataTable.listFamily, RemoteDataTable.dataColumn, rawData.getDataStr()));
            rowDao.insert(RemoteDataTable.tableName, puts); //insert all
            //hdfs
            metaPkg.setFiles(trainingDatas);
        }
        //hdfs
        ((HdfsFetchTTPkg)fetchPkg).getMetaPkgs().add(metaPkg);
    }

    /**
     * 测试集数据预处理
     * @param fetchPkg
     * @param date
     * @param metadata
     * @throws IOException
     */
    private void test(IPackage fetchPkg, String date, MessageAndMetadata metadata) throws IOException {
        //解析MessageAndMetadata
        TrainingRawData rawData = null;
//        TrainingRawData rawData = JSONObject.parseObject(metadata.getPayload(), TrainingRawData.class);
        String id = rawData.getId(); //表单id，全局唯一
        String uid = rawData.getClientId(); //user id
        String type = rawData.getType(); //data type
        String url = rawData.getUrl(); //data dir url

        //rowkey设计需考虑“过热”问题
        String rowKey = id + "_" + date + "_" + uid;

        //hdfs
        HdfsFetchTTMetaPkg metaPkg = new HdfsFetchTTMetaPkg(uid, type, url);
        //每个任务包都有一个唯一的id
        metaPkg.setId(id);

        //step1：去重
        List<Result> querys = rowDao.queryByKeyPrefix(TestDataTable.tableName, id + "");
        if (querys.size() > 0) {
            System.out.println("id=" + id + "的表单在TestDataTable中已存在！此处不做处理返回！");
            return;
        }

        //step2：如果不重复，则插入HBase的remote_data表中
        //list of testingdata
        List<TrainingData> testingingDatas = rawData.getDatas();
        if (testingingDatas != null) {
            List<Put> puts = new ArrayList<>();
            puts.add(getPut(rowKey, TestDataTable.infoFamily, TestDataTable.idColumn, id));
            puts.add(getPut(rowKey, TestDataTable.infoFamily, TestDataTable.typeColumn, type));
            puts.add(getPut(rowKey, TestDataTable.infoFamily, TestDataTable.urlColumn, url));
            puts.add(getPut(rowKey, TestDataTable.infoFamily, TestDataTable.clientIdColumn, uid + ""));
            puts.add(getPut(rowKey, TestDataTable.listFamily, TestDataTable.dataColumn, rawData.getDataStr()));
            rowDao.insert(TestDataTable.tableName, puts); //insert all
            //hdfs
            metaPkg.setFiles(testingingDatas);
        }
        //hdfs
        ((HdfsFetchTTPkg)fetchPkg).getMetaPkgs().add(metaPkg);
    }

    /**
     * 标签同步数据save or update
     * TODO 一旦更新tag列表，则必须重新训练模型！！！--> 如何控制？！
     * @param fetchPkg
     * @param date
     * @param metadata
     * @throws IOException
     */
    private void tagList(IPackage fetchPkg, String date, MessageAndMetadata metadata) throws IOException {
//        HdfsFetchTagListPkg pkg = (HdfsFetchTagListPkg) fetchPkg;
//        String rowKey = pkg.getType();
        TagsRawData rawData = null;
//        TagsRawData rawData = JSONObject.parseObject(metadata.getPayload(), TagsRawData.class);
        String id = rawData.getId();
        List<TagsTypeData> typeDatas = rawData.getDatas();
        for (TagsTypeData typeData : typeDatas) {
            //将每种试验类型的标签持久化到mysql的tag表[id, tagId, type, content, tag]
            tagSyncDispatcher.dispatch(typeData);
        }
    }
    /**
     * 获取HBase批量插入put
     * @param key
     * @param family
     * @param column
     * @param value
     * @return
     */
    private Put getPut(String key, String family, String column, String value) {
        Put put = new Put(Bytes.toBytes(key));
        put.addColumn(Bytes.toBytes(family),
                        Bytes.toBytes(column),
                        Bytes.toBytes(value));
        return put;
    }

}

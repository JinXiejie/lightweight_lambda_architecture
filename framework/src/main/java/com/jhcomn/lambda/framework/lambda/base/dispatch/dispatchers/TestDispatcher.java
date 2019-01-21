package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector.impl.TestFSDataInputStreamCallbackImpl;
import com.jhcomn.lambda.framework.lambda.persistence.client.DaoClientFactory;
import com.jhcomn.lambda.framework.lambda.persistence.client.IDaoClient;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.mllib.base.IMLTrainingController;
import com.jhcomn.lambda.mllib.recommend.UserRecommender;
import com.jhcomn.lambda.packages.test.UserMLResultPackage;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Test Package 结构化保存至Parquet实际操作类
 * Created by shimn on 2016/12/27.
 */
public class TestDispatcher extends BaseDispatcher {

    private static final Logger log = LoggerFactory.getLogger(TestDispatcher.class);

    private IMLTrainingController imlTrainingController;

    public TestDispatcher() {
    }

//    @Override
//    public void saveAndUpdate(String date, IPackage ipkg, PackageType type, boolean isSpeed) {
//        super.saveAndUpdate(date, ipkg, type, isSpeed);
//        //分发任务处理机制
//        System.out.println("batch layer start computation, is speed : " + isSpeed);
//        if (isSpeed) {
//            incrementalCompute(date, (HdfsFetchTTMetaPkg) ipkg);
//        }
//        else {
//            System.out.println("batch layer start full computation now.");
//            fullCompute(date, (BatchUpdatePkg) ipkg);
//        }
//    }

    /**
     * 增量计算
     * @param date
     * @param metaPkg
     */
    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {

        System.out.println("speed layer start incremental computation, date is " + date);

        String path = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.HDFS_RAW_DATA_PATH;

        /*
         *  注意：URI规范中，文件路径不能包含符号":"（格式："yyyy-MM-dd HH:mm:ss"）
         *  现在parquet路径定义为：/path/key=yyyyMMddHHmmss
         */
        String[] dateArr = date.split(" ");
        String key = dateArr[0].replaceAll("-", "") + dateArr[1].replaceAll(":", "");
        String parquetPath = path + Properties.PARQUET_DATA_PATH + "/key=" + key; //../key=speed/yyyy-MM-dd HH:mm:ss

        String cachePath = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.RESULT_DATA_PATH
                + Properties.SPEED_DATA_PATH
                + "/key=" + key;

        String modelPath = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.MODEL_DATA_PATH
                + Properties.SPEED_DATA_PATH
                + "/key=" + key;

        System.out.println("parquetPath = " + parquetPath);

        metaPkg.getFiles().stream().filter(pkg -> pkg != null).forEach(pkg -> {
            System.out.println("read the file from HDFS : " + path + "/" + pkg.getFile());
            //saveOrUpdate to HBase all time : 359419
            //saveOrUpdate to Parquet all time : 78398
            long begin = System.currentTimeMillis();
            System.out.println("saveOrUpdate to Parquet begin now ----> ");
            //TODO kafka-spark-consumer 接入可能存在问题：1.存在重复消费；2.宕机后重启，无法接入消费原先topic的内容，新建新topic则正常
            //TODO bug of broking here
            /**
             * saveOrUpdate to Parquet
             */
            readWriteDao.readFile(path + "/" + pkg.getFile(),
                    new TestFSDataInputStreamCallbackImpl(spark, parquetPath));

            System.out.println("saveOrUpdate to Parquet all time : " + (System.currentTimeMillis()-begin));

            /**
             * 推荐算法测试
             * 2017/1/5
             * shimn
             */
            imlTrainingController = new UserRecommender(spark,
                    metaPkg.getId(),
                    parquetPath,
                    modelPath,
                    cachePath);
            imlTrainingController.start();
            while (imlTrainingController.getResult() == null);
            System.out.println("增量计算：User Recommend mission is finished. Prepare for persisting into HBase now.");
            IDaoClient daoClient = DaoClientFactory.createHBaseDaoClient();
            daoClient.save(imlTrainingController.getResult());
        });

    }

    /**
     * 全量计算
     * @param date
     * @param ipkg
     */
    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {

        System.out.println("batch layer start full computation, date is " + date);

        String dir = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.HDFS_RAW_DATA_PATH;

        String parquetPath = dir + Properties.PARQUET_DATA_PATH;

        String cachePath = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.RESULT_DATA_PATH
                + "/key=parquet";

        String modelPath = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.MODEL_DATA_PATH;

        //导出原始数据
        long begin = System.currentTimeMillis();
//        System.out.println("现在导出原始数据");
//        Dataset<Row> rawDF = spark.read().parquet(parquetPath);
////        FSDataOutputStream fos = readWriteDao.create(new Path(modelPath + "/user.txt"));
//        Dataset<String> df = rawDF.map(new MapFunction<Row, String>() {
//            @Override
//            public String call(Row row) throws Exception {
//                String str = row.getString(6) + "," + row.getString(0) + "," + row.getString(1) + ","
//                        + row.getString(2) + "," + row.getString(3) + "," + row.getString(4) + ","
//                        + row.getString(5) + ",0," + row.getString(7) + "," + row.getString(8) + ","
//                        + "no name,C4CA4238A0B923820DCC509A6F75849B,,default.png," + row.getString(9) + "\n";
////                fos.write(str.getBytes());
////                readWriteDao.write(modelPath + "/user.txt", str);
//                return str;
//            }
//        }, Encoders.STRING());
//        df.rdd().saveAsTextFile(modelPath + "/user.txt");
//        String[] lists = df.collect();
//        System.out.println("lists size = " + lists.length);
//        for (int i = 0; i < lists.length; i++) {
//            try {
//                fos.writeBytes(lists[i]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        System.out.println("导出原始数据结束：" + (System.currentTimeMillis() - begin));
//        try {
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /**
         * 推荐算法测试
         * 2017/1/13
         * shimn
         */
        imlTrainingController = new UserRecommender(spark,
                                            ipkg.getId(),
                                            parquetPath,
                                            modelPath,
                                            cachePath);
        imlTrainingController.start();
        while (imlTrainingController.getResult() == null);
        System.out.println("全量计算：User Recommend mission is finished. Prepare for persisting into HBase now.");
        IDaoClient daoClient = DaoClientFactory.createHBaseDaoClient();
        daoClient.save(imlTrainingController.getResult());



        //导出结果文件
        begin = System.currentTimeMillis();
        System.out.println("现在导出结果文件");
        String resultPath = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + "/result.txt";
        FSDataOutputStream fosR = readWriteDao.create(new Path(resultPath));
        UserMLResultPackage userResult = (UserMLResultPackage) imlTrainingController.getResult();
        Set<Long> sets = userResult.getResults().keySet();
        for (Long key : sets) {
            try {
                fosR.write((key + "," + userResult.getResults().get(key) + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("导出结果数据结束：" + (System.currentTimeMillis() - begin));
        try {
            fosR.flush();
            fosR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //移除原来的key=batch的parquet
//        if (readWriteDao.exists(parquetPath))
//            readWriteDao.delete(parquetPath);
//
//        Path[] paths = readWriteDao.listAllFile(new Path(dir + Properties.HDFS_RAW_DATA_PATH));
//        for (Path path : paths) {
//            System.out.println(path.toString());
//            /**
//             * saveOrUpdate to parquet
//             */
//            readWriteDao.readFile(path.toString(),
//                    new TestFSDataInputStreamCallbackImpl(spark, parquetPath));
//        }
    }

    @Override
    protected void analyze(String date, HdfsFetchTTMetaPkg metaPkg) {

    }

}

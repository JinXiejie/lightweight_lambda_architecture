package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.alibaba.fastjson.JSON;
import com.jhcomn.lambda.framework.kafka.util.producer.models.ProducerPackage;
import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.framework.utils.StringUtil;
import com.jhcomn.lambda.mllib.base.callback.PreprocessCallback;
import com.jhcomn.lambda.mllib.base.callback.ProcessCallback;
import com.jhcomn.lambda.mllib.base.callback.TrainingCallback;
import com.jhcomn.lambda.mllib.helloworld.HelloWorldMLController;
import com.jhcomn.lambda.mllib.helloworld.HelloWorldResult;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.ml_model.SyncMLModel;
import com.jhcomn.lambda.packages.result.ProcessResultModel;
import com.jhcomn.lambda.packages.result.ResultModel;
import com.jhcomn.lambda.packages.result.SqlResultModel;
import com.jhcomn.lambda.packages.result.helloworld.HelloWorldProcessResultModel;
import com.jhcomn.lambda.packages.result.helloworld.HelloWorldResultModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * hello-world demo处理器
 * Created by shimn on 2017/4/20.
 */
public class HelloWorldDispatcher extends BaseDispatcher {

    public HelloWorldDispatcher() {
        imlController = new HelloWorldMLController(spark, fileSystem);
    }

    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {
        super.incrementalCompute(date, metaPkg);
    }

    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {
        System.out.println("batch layer start full computation, date is " + date);

        String dir = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.HDFS_RAW_TRAIN_DATA_PATH;

        String parquetPath = dir + Properties.PARQUET_DATA_PATH;

        //只写模型
        String modelPath = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.MODEL_DATA_PATH;

        //只读模型
        String modelBackupPath = Properties.ROOT_DATA_PATH
                + "/" + ipkg.getUid()
                + "/" + ipkg.getType()
                + Properties.MODEL_BACKUP_DATA_PATH;

        long begin = System.currentTimeMillis();
        System.out.println("开始全量数据模型训练");
        /**
         * 全量数据训练模型
         */
//        validate(parquetPath);
//        validate(modelPath);
//        validate(modelBackupPath);
        imlController.train(parquetPath, modelPath, modelBackupPath,
                new TrainingCallback() {

                    @Override
                    public void onStart(String msg) {
                        //TODO 开始全量数据模型训练，需要先备份模型
                        System.out.println(msg);
                    }

                    @Override
                    public void onStop(String msg) {
                        //TODO 结束全量数据模型训练，需要同步模型; 此处的msg应该是model转json后的数据模型
                        //TODO 1.主从模型最终一致性 --> 交给业务消化
                        String time = StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
                        SyncMLModel model = new SyncMLModel(PackageType.HELLO_WORLD.value() + "",
                                ipkg.getUid(), ipkg.getType(), time);
                        model.model = msg;
//                        System.out.println("json：" + msg);
//                        System.out.println("model：" + model.toString());
                        System.out.println("更新mysql数据库的json模型: " + model.toString());
                        //TODO 2.更新mysql数据库的json模型
                        if (syncController != null)
                            syncController.saveOrUpdate(model);
                        else
                            System.out.println("syncController is null");
                    }

                    @Override
                    public void onProgress(float progress) {
                        System.out.println("训练进度 = " + progress);
                    }

                    @Override
                    public void onOverwriteModel(String modelPath, String modelBackupPath) {
                        //TODO 覆盖模型，此处删除路径下所有文件夹及文件
                        if (readWriteDao.exists(modelPath)) {
                            readWriteDao.delete(modelPath);
                        }
                        if (readWriteDao.exists(modelBackupPath)) {
                            readWriteDao.delete(modelBackupPath);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println(error);
                    }
                });
        System.out.println("全量数据模型训练结束，用时 = " + (System.currentTimeMillis() - begin));

    }


    @Override
    protected void analyze(String date, HdfsFetchTTMetaPkg metaPkg) {
        if (metaPkg == null) {
            System.out.println("metaPkg is null");
            return;
        }
        String path = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.HDFS_RAW_TEST_DATA_PATH;

        String modelBackupPath = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.MODEL_BACKUP_DATA_PATH;

        String resultPath = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.RESULT_DATA_PATH
                + Properties.PARQUET_DATA_PATH;

        validate(path);

        /*
         *  注意：URI规范中，文件路径不能包含符号":"（格式："yyyy-MM-dd HH:mm:ss"）
         *  现在parquet路径定义为：/path/key=yyyyMMddHHmmss
         */
        String[] dateArr = date.split(" ");
        String dateKey = dateArr[0].replaceAll("-", "") + dateArr[1].replaceAll(":", "");
        String parquetPath = path + Properties.PARQUET_DATA_PATH + "/key=" + dateKey; //../key=speed/yyyy-MM-dd HH:mm:ss
        //System.out.println("parquetPath = " + parquetPath);

        List<HelloWorldProcessResultModel> results = new ArrayList<>();
        HelloWorldResultModel out = new HelloWorldResultModel(metaPkg.getId(),
                metaPkg.getUid(), metaPkg.getType(), results);

        metaPkg.getFiles().stream().filter(pkg -> pkg != null).forEach(pkg -> {
            HelloWorldProcessResultModel processResult = new HelloWorldProcessResultModel();
            processResult.setFile(pkg.getFile());

            System.out.println("read the file from HDFS : " + path + "/" + pkg.getFile());
            long begin = System.currentTimeMillis();
            //System.out.println("saveOrUpdate to Parquet begin now ----> ");
            /**
             * 数据预处理：saveOrUpdate to Parquet
             * 法2：传入文件路径，使用textfile读取
             */
            List<IPackage> lists = imlController.preprocessWithUrl(false, path + "/" + pkg.getFile(), null, parquetPath,
                    new PreprocessCallback() {
                        @Override
                        public void onStart(String msg) {
                            System.out.println(msg);
                        }

                        @Override
                        public void onError(String error) {
                            System.out.println(error);
                        }

                        @Override
                        public void onStop(String msg) {
                            System.out.println(msg);
                        }
                    });

            System.out.println("saveOrUpdate to Parquet all time : " + (System.currentTimeMillis()-begin));
            System.out.println("开始测试各国家足球数据在亚洲的水平::size:" + lists.size());
            if (lists != null) {
                for (IPackage model : lists) {
                    IPackage result = imlController.analyze(modelBackupPath, model,
                            new ProcessCallback() {
                                @Override
                                public void onStart(String msg) {
                                    System.out.println(msg);
                                }

                                @Override
                                public void onError(String error) {
                                    System.out.println(error);
                                }

                                @Override
                                public void onStop(String msg) {
                                    System.out.println(msg);
                                }
                            });
                    System.out.println("tag = " + ((HelloWorldResult)result).kind());
                    processResult.setTag(((HelloWorldResult)result).kind());
                    results.add(processResult);
                }
            }
        });

        out.setDatas(results);

        /**
         * TODO
         * 1.out send to kafka broker --> 格式:ProducerPackage
         * 2.out save to mysql (metaPkg's id is primary key) --> 格式:SqlResultModel
         */
        String json = JSON.toJSONString(out);
        System.out.println("json result = \n" + json);
        ProducerPackage producerPackage = new ProducerPackage(ConstantDatas.HELLO_WORLD_RESULT, out.getId(), json);
        SqlResultModel sqlResultModel = new SqlResultModel(out.getId(), out.getClientId(), out.getType(),
                JSON.toJSONString(out.getDatas()));
        if (servingController != null) {
            try {
                servingController.send(producerPackage);    //1
                servingController.saveOrUpdate(sqlResultModel);    //2
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        else
            System.out.println("servingController is null");

        System.out.println("result save or update in mysql and send to kafka broker end...");
    }

//    List<ResultModel> outputResultList = new ArrayList<>();
//    outputResultList.add(out);
//    write2Parquet(outputResultList, resultPath);
//    private void write2Parquet(List<ResultModel> lists, String resultPath) {
//        try {
//            System.out.println("write result into parquet=" + resultPath);
//            Dataset<Row> df = spark.createDataFrame(lists, ResultModel.class);
//            df.write().mode(SaveMode.Append).parquet(resultPath);
//            System.out.println("write result into parquet finished.");
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//    }
}

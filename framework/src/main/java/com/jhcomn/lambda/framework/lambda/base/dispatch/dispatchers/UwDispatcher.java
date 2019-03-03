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
import com.jhcomn.lambda.mllib.helloworld.HelloWorldResult;
import com.jhcomn.lambda.mllib.uw1000.UW1000Controller;
import com.jhcomn.lambda.mllib.uw1000.UW1000Feature;
import com.jhcomn.lambda.mllib.uw1000.UW1000Result;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.ml_model.SyncMLModel;
import com.jhcomn.lambda.packages.result.SqlResultModel;
import com.jhcomn.lambda.packages.result.uw1000.UWProcessResultModel;
import com.jhcomn.lambda.packages.result.uw1000.UWResultModel;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * uw1000超声波业务算法路由
 * Created by shimn on 2016/12/27.
 */
public class UwDispatcher extends BaseDispatcher {

    public UwDispatcher() {
        imlController = new UW1000Controller(spark, fileSystem);
    }

    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {
        super.incrementalCompute(date, metaPkg);
    }

    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {
        System.out.println("batch layer start UW full computation, date is " + date);

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
        imlController.train(parquetPath, modelPath, modelBackupPath,
                new TrainingCallback() {
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
                    public void onProgress(float progress) {
                        System.out.println("训练进度 = " + progress);
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println(error);
                    }

                    @Override
                    public void onStart(String msg) {
                        //TODO 开始全量数据模型训练，需要先备份模型
                        System.out.println(msg);
                    }

                    @Override
                    public void onStop(String msg) {
                        //TODO 结束全量数据模型训练，需要同步模型; 此处的msg是model在hdfs下路径的元信息
                        //1.拉取hdfs上的uw模型到本地
                        String localPath = Properties.LOCAL_MODEL_PATH + "/uw1000/uw1000_tags.model";
                        String jsonStr = JSON.toJSONString(localPath);
                        readWriteDao.download(new Path(msg), new Path(localPath));
                        System.out.println("模型下载到本地成功...");
                        //2.主从模型最终一致性 --> 交给业务消化
                        String time = StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
                        SyncMLModel model = new SyncMLModel(PackageType.UW.value() + "",
                                ipkg.getUid(), ipkg.getType(), time);
                        model.model = jsonStr;
//                        System.out.println("model：" + model.toString());
                        System.out.println("更新mysql数据库的json模型: " + model.toString());
                        //3.更新mysql数据库的json模型
                        if (syncController != null)
                            syncController.saveOrUpdate(model);
                        else
                            System.out.println("syncController is null");
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

        //只读模型路径
        String modelBackupPath = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.MODEL_BACKUP_DATA_PATH;

        /*
         *  注意：URI规范中，文件路径不能包含符号":"（格式："yyyy-MM-dd HH:mm:ss"）
         *  现在parquet路径定义为：/path/key=yyyyMMddHHmmss
         */
        String[] dateArr = date.split(" ");
        String dateKey = dateArr[0].replaceAll("-", "") + dateArr[1].replaceAll(":", "");
        String parquetPath = path + Properties.PARQUET_DATA_PATH + "/key=" + dateKey; //../key=speed/yyyy-MM-dd HH:mm:ss

        List<UWProcessResultModel> results = new ArrayList<>();
        UWResultModel out = new UWResultModel(metaPkg.getId(),
                metaPkg.getUid(), metaPkg.getType(), results);

        metaPkg.getFiles().stream().filter(pkg -> pkg != null).forEach(pkg -> {
            //用于存储处理结果的model
            UWProcessResultModel processResult = new UWProcessResultModel();
            processResult.setFile(pkg.getFile());

            System.out.println("read the file from HDFS : " + path + "/" + pkg.getFile());
            long begin = System.currentTimeMillis();
            /**
             * 数据预处理：saveOrUpdate to Parquet
             * 法2：传入文件路径，使用textfile读取
             */
            List<IPackage> lists = imlController.preprocessWithUrl(true, path + "/" + pkg.getFile(), null, parquetPath,
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
            System.out.println("开始调用模型分析超声波音频:" + lists.size());

            if (lists != null) {
                for (IPackage data : lists) {
                    IPackage result = imlController.analyze(modelBackupPath, data,
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
                    if (result != null) {
                        processResult.setTag(((UW1000Result)result).getTags());
                    }
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
        ProducerPackage producerPackage = new ProducerPackage(ConstantDatas.UW_RESULT, out.getId(), json);
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

        System.out.println("UW result save or update in mysql and send to kafka broker end...");
    }
}

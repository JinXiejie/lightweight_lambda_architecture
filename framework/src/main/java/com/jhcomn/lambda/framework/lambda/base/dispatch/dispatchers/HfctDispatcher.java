package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.alibaba.fastjson.JSON;
import com.jhcomn.lambda.framework.kafka.util.producer.models.ProducerPackage;
import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.framework.utils.StringUtil;
import com.jhcomn.lambda.mllib.base.callback.ProcessCallback;
import com.jhcomn.lambda.mllib.base.callback.TrainingCallback;
import com.jhcomn.lambda.mllib.hfct1500.HFCT1500Controller;
import com.jhcomn.lambda.mllib.hfct1500.HFCTResult;
import com.jhcomn.lambda.mllib.hfct1500.classify.HFCTClassifyMetaMessage;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.ml_model.SyncMLModel;
import com.jhcomn.lambda.packages.result.hfct1500.HfctProcessResultModel;
import com.jhcomn.lambda.packages.result.ProcessResultModel;
import com.jhcomn.lambda.packages.result.ResultModel;
import com.jhcomn.lambda.packages.result.SqlResultModel;
import com.jhcomn.lambda.packages.result.hfct1500.HfctResultModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * HFCT1500业务算法路由
 * Created by shimn on 2016/12/27.
 */
public class HfctDispatcher extends BaseDispatcher {

    public HfctDispatcher() {
        imlController = new HFCT1500Controller(spark, fileSystem);
    }

    //训练集数据预处理
    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {
        super.incrementalCompute(date, metaPkg);
    }

    //训练模型
    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {
        System.out.println("batch layer start HFCT full computation, date is " + date);

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
                        //TODO 结束全量数据模型训练，需要同步模型; 此处的msg应该是model转json后的数据模型
                        //TODO 1.主从模型最终一致性 --> 交给业务消化
                        String time = StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
                        SyncMLModel model = new SyncMLModel(PackageType.HFCT.value() + "",
                                ipkg.getUid(), ipkg.getType(), time);
                        model.model = msg;
//                        System.out.println("json：" + msg);
//                        System.out.println("model：" + model.toString());
//                        System.out.println("更新mysql数据库的json模型: " + model.toString());
                        System.out.println("更新mysql数据库的json模型");
                        //TODO 2.更新mysql数据库的json模型
                        if (syncController != null)
                            syncController.saveOrUpdate(model);
                        else
                            System.out.println("syncController is null");
                    }
                });
        System.out.println("全量数据模型训练结束，用时 = " + (System.currentTimeMillis() - begin));
    }

    //智能分析，测试集数据
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

        List<HfctProcessResultModel> results = new ArrayList<>();
        HfctResultModel out = new HfctResultModel(metaPkg.getId(),
                metaPkg.getUid(), metaPkg.getType(), results);

        metaPkg.getFiles().stream().filter(pkg -> pkg != null).forEach(pkg -> {
            //用于存储处理结果的model
            HfctProcessResultModel processResult = new HfctProcessResultModel();
            processResult.setFile(pkg.getFile());

            System.out.println("read the file from HDFS : " + path + "/" + pkg.getFile());
//            long begin = System.currentTimeMillis();
            /**
             * 由于HFCT分类算法特殊，故此处不作与训练数据预处理一致的方法，直接调用analyze方法即可
             * 且只需传入测试文件所在HDFS路径即可
             */
            IPackage result = imlController.analyze(modelBackupPath,
                    new HFCTClassifyMetaMessage(path + "/" + pkg.getFile()),
                    new ProcessCallback() {
                        @Override
                        public void onError(String error) {
                            System.out.println(error);
                        }

                        @Override
                        public void onStart(String msg) {
                            System.out.println(msg);
                        }

                        @Override
                        public void onStop(String msg) {
                            System.out.println(msg);
                        }
                    });
            if (result != null) {
                processResult.setTag(((HFCTResult)result).getTags());
            }
            results.add(processResult);
        });

        out.setDatas(results);

        /**
         * TODO
         * 1.out send to kafka broker --> 格式:ProducerPackage
         * 2.out save to mysql (metaPkg's id is primary key) --> 格式:SqlResultModel
         */
        String json = JSON.toJSONString(out);
        System.out.println("json result = \n" + json);
        ProducerPackage producerPackage = new ProducerPackage(ConstantDatas.HFCT_RESULT, out.getId(), json);
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

        System.out.println("HFCT result save or update in mysql and send to kafka broker end...");

    }
}

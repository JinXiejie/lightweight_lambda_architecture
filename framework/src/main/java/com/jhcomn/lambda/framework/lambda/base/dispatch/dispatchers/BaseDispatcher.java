package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector.IHdfs2Parquet;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.framework.lambda.serving.result.base.IServingController;
import com.jhcomn.lambda.framework.lambda.serving.result.controller.ResultServingController;
import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.framework.lambda.sync.model.ModelSyncController;
import com.jhcomn.lambda.framework.spark.base.SparkBaseManager;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadWriteHdfsDao;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadOnlyHdfsDaoImpl;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadWriteHdfsDaoImpl;
import com.jhcomn.lambda.mllib.base.IMLController;
import com.jhcomn.lambda.mllib.base.callback.PreprocessCallback;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.sql.SparkSession;

/**
 * 数据包分发器父类
 * Created by shimn on 2016/12/27.
 */
public abstract class BaseDispatcher implements IHdfs2Parquet {

    protected SparkSession spark = null;
    protected FileSystem fileSystem = null;
    protected ReadWriteHdfsDao readWriteDao = null;

    protected ISyncController syncController = null;
    protected IServingController servingController = null;

    protected IMLController imlController = null;

    public BaseDispatcher() {
        try {
            spark = SparkBaseManager.buildSparkSession();
            fileSystem = FileSystem.get(SparkBaseManager.buildHadoopConfiguration());
            readWriteDao = new ReadWriteHdfsDaoImpl(fileSystem, new ReadOnlyHdfsDaoImpl(fileSystem));

            syncController = new ModelSyncController();
            servingController = new ResultServingController();
        } catch (Exception e) {
            System.out.println("BaseDispatcher init error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检验目录是否创建
     */
    protected void validate(String path) {
        if (readWriteDao != null) {
            if (!readWriteDao.exists(path)) {
                readWriteDao.mkdirs(path);
            }
        }
    }

    @Override
    public void saveAndUpdate(String date, IPackage ipkg, PackageType type, boolean isSpeed, String key) {
        //分发任务处理机制
        System.out.println("dispatcher start computation, is speed : " + isSpeed);
        if (key != null && !key.equals("")) {
            if (key.equals(ConstantDatas.TRAINING)) {
                //训练
                if (isSpeed) {
                    incrementalCompute(date, (HdfsFetchTTMetaPkg) ipkg);
                }
                else {
                    System.out.println("batch layer start full computation now.");
                    fullCompute(date, (BatchUpdatePkg) ipkg);
                }
            }
            else if (key.equals(ConstantDatas.TESTING)) {
                //分析
                System.out.println("智能分析begin now");
                analyze(date, (HdfsFetchTTMetaPkg) ipkg);
            }
        }
    }

    //增量计算 --> 用于训练
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {
        System.out.println("speed layer start incremental computation, date is " + date);

        String path = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.HDFS_RAW_TRAIN_DATA_PATH;

        validate(path);

        /*
         *  注意：URI规范中，文件路径不能包含符号":"（格式："yyyy-MM-dd HH:mm:ss"）
         *  现在parquet路径定义为：/path/key=yyyyMMddHHmmss
         */
        String[] dateArr = date.split(" ");
        String key = dateArr[0].replaceAll("-", "") + dateArr[1].replaceAll(":", "");
        String parquetPath = path + Properties.PARQUET_DATA_PATH + "/key=" + key; //../key=speed/yyyy-MM-dd HH:mm:ss
        System.out.println("parquetPath = " + parquetPath);

        if (imlController == null) {
            System.out.println("imlController is null, return now");
            return;
        }
        metaPkg.getFiles().stream().filter(pkg -> pkg != null).forEach(pkg -> {
            System.out.println("read the file from HDFS : " + path + "/" + pkg.getFile());
            long begin = System.currentTimeMillis();
            System.out.println("saveOrUpdate to Parquet begin now ----> ");
            /**
             * saveOrUpdate to Parquet
             */
            String tag = pkg.getTag_id() + ""; //tag用tag_id
            /**
             * 法1：传入文件数据流
             */
//            readWriteDao.readFile(path + "/" + pkg.getFile(),
//                    new HelloWorldFSDataInputStreamCallbackImpl(imlController, tag, parquetPath));

            /**
             * 法2：传入文件路径，使用textfile读取
             * + "." + pkg.getTypeName()
             */
            imlController.preprocessWithUrl(true, path + "/" + pkg.getFile(), tag, parquetPath,
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

        });
    }

    //全量计算 --> 用于训练
    protected abstract void fullCompute(String date, BatchUpdatePkg ipkg);

    //智能分析结果 --> 用于测试
    protected abstract void analyze(String date, HdfsFetchTTMetaPkg metaPkg);
}

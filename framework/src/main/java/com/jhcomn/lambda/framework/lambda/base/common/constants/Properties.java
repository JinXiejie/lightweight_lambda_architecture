package com.jhcomn.lambda.framework.lambda.base.common.constants;

/**
 * 基本属性
 * Created by shimn on 2016/12/27.
 */
public class Properties {

    public static final String ROOT_PATH = "/lambda_architecture";

    public static final String ROOT_DATA_PATH = ROOT_PATH + "/data";

    public static final String DEFAUL_USER_DATA_PATH = ROOT_DATA_PATH + "/1"; //默认客户Id为1

    public static final String BATCH_DATA_PATH = "/batch";

    public static final String SPEED_DATA_PATH = "/speed";

    public static final String HDFS_RAW_DATA_PATH = "/raw";

    public static final String HDFS_RAW_TRAIN_DATA_PATH = HDFS_RAW_DATA_PATH + "/" + ConstantDatas.TRAINING;

    public static final String HDFS_RAW_TEST_DATA_PATH = HDFS_RAW_DATA_PATH +  "/" + ConstantDatas.TESTING;

    public static final String PARQUET_DATA_PATH = "/parquet";

    public static final String RESULT_DATA_PATH = "/result";

    public static final String MODEL_DATA_PATH = "/model";

    public static final String MODEL_BACKUP_DATA_PATH = "/model_backup";

    public static final String JSON_MODEL_PATH = "/json";

    public static final String SERIALIZE_MODEL_PATH = "/serialize";

    public static final String PARQUET_BATCH_DATA_PATH = PARQUET_DATA_PATH + "/key=batch";

    public static final String PARQUET_SPEED_DATA_PATH = PARQUET_DATA_PATH + "/key=speed";

    public static final String PARQUET_SPEED_SUFFIX = "_speed.parquet";

    public static final String PARQUET_BATCH_SUFFIX = "_batch.parquet";

    public static String BATCH_WORK_TIME = "00:00:00";

    public static String LOCAL_MODEL_PATH = "/usr/local/platformTest/mlmodels";

}

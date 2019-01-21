package com.jhcomn.lambda.framework.lambda.base.common.constants;

/**
 * Created by shimn on 2016/12/30.
 */
public class ConstantDatas {

    public static final int HBASE_FLUSH_SIZE = 10000; //1w

    public static final int PARQUET_FLUSH_SIZE = 100000; //10w --> 经验值

    public static final String BATCH_LAYER_NAME = "BatchLayer";
    public static final String SPEED_LAYER_NAME = "SpeedLayer";
    public static final String SERVING_LAYER_NAME = "ServingLayer";

    //kafka different keys of the same topic
    public static final String TRAINING = "train";
    public static final String TESTING = "test";
    public static final String TAGS = "tag";

    //kafka topic #web->platform#
    public static final String UW = "UW";
    public static final String TEV = "TEV";
    public static final String INFRARED = "INFRARED";
    public static final String HFCT = "HFCT";
    public static final String TEST = "TEST";
    public static final String HELLO_WORLD = "HELLO_WORLD";
    public static final String UHF = "UHF";

    //kafka topic #platform->web#
    public static final String UW_RESULT = "UW-RESULT";
    public static final String TEV_RESULT = "TEV-RESULT";
    public static final String INFRARED_RESULT = "INFRARED-RESULT";
    public static final String HFCT_RESULT = "HFCT-RESULT";
    public static final String TEST_RESULT = "TEST-RESULT";
    public static final String HELLO_WORLD_RESULT = "HELLO_WORLD-RESULT";
    public static final String UHF_RESULT = "UHF-RESULT";
}

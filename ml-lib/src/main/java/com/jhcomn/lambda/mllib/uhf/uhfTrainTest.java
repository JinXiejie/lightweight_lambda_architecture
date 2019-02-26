package com.jhcomn.lambda.mllib.uhf;

import com.jhcomn.lambda.mllib.uhf.preprocess.UHFAnalyze;

public class uhfTrainTest {
    public static void main(String[] args){
        String jsonStr = "{\n" +
                "\t\"clientId\": 4,\n" +
                "\t\"datas\": [{\n" +
                "\t\t\"file\": \"100062313506259.jpg\",\n" +
                "\t\t\"typeName\": \"UW\",\n" +
                "\t\t\"typeId\": \"2\"\n" +
                "\t}],\n" +
                "\t\"id\": 100232410385469,\n" +
                "\t\"type\": \"test-UW\",\n" +
                "\t\"url\": \"http://www.youdiancloud.com/epcbm/biz/app/machineLearn.do?action=analyzeTempFile&fileName=\"\n" +
                "}";
//        UHFAnalyze uhfAnalyze = new UHFAnalyze();
//        uhfAnalyze.uhfTrain(jsonStr);
    }
}

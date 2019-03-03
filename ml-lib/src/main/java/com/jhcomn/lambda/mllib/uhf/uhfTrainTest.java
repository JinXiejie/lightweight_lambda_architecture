package com.jhcomn.lambda.mllib.uhf;

import com.jhcomn.lambda.mllib.uhf.preprocess.UHFAnalyze;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.KafkaReceiver;

public class uhfTrainTest {
    public static void main(String[] args) {
//        String jsonStr = "{\n" +
//                "\t\"clientId\": 4,\n" +
//                "\t\"datas\": [{\n" +
//                "\t\t\"file\": \"100062313506259.jpg\",\n" +
//                "\t\t\"typeName\": \"UW\",\n" +
//                "\t\t\"typeId\": \"2\"\n" +
//                "\t}],\n" +
//                "\t\"id\": 100232410385469,\n" +
//                "\t\"type\": \"test-UW\",\n" +
//                "\t\"url\": \"http://www.youdiancloud.com/epcbm/biz/app/machineLearn.do?action=analyzeTempFile&fileName=\"\n" +
//                "}";

        String jsonStr = "{\n" +
                "\t\"clientId\": 4,\n" +
                "\t\"datas\": [{\n" +
                "\t\t\"file\": \"100062313506259.jpg\",\n" +
                "\t\t\"typeName\": \"UHF\",\n" +
                "\t\t\"typeId\": \"2\"\n" +
                "\t}],\n" +
                "\t\"id\": 100232410385469,\n" +
                "\t\"type\": \"test-UW\",\n" +
                "\t\"url\": \"http://www.youdiancloud.com/PDMSystem-PdmSys-CouplerSPDC0001_01_20140611235118.dat\"\n" +
                "}";
        String topic = "UHF";
        String key = "test";
        UHFAnalyze uhfAnalyze = new UHFAnalyze(topic, key);
        uhfAnalyze.receive();
        uhfAnalyze.uhfTest(jsonStr);


//        uhfAnalyze.uhfTrain();
//        uhfAnalyze.send(topic, "value");


//        KafkaReceiver receiver = KafkaReceiver.getInstance();
//        receiver.Receive();


//        KafkaKeySender sender = KafkaKeySender.getInstance();
//        sender.Send(key, topic);
    }
}

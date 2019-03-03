package com.jhcomn.lambda.mllib.uhf.preprocess;

//import com.alibaba.fastjson.JSONObject;
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader.UHFDataReaderDat;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.AnalyseTask;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.KafkaKeySender;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.KafkaReceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import net.sf.json.JSONObject;

public class UHFAnalyze {
    private KafkaKeySender sender = KafkaKeySender.getInstance();
    public UHFDataReaderDat uhfDataReaderDat = new UHFDataReaderDat();
    private KafkaReceiver receiver = null;
//    private static String pythonExePath = "/home/jhcomn/anaconda3/bin/python";
//    private static String pythonExePath = "D:\\ProgramData\\Anaconda2\\envs\\tensorflow_env\\python.exe";
    private static String pythonExePath = "E:\\JinXiejie\\Anaconda3\\python.exe";
    private String topic = null;
    private String key = null;
    public String jsonStr = null;
    public String analyzeTopic = null;

    public void kafkaInstance() {
        sender = KafkaKeySender.getInstance();
        receiver = KafkaReceiver.getInstance();
        receive();
    }

    public UHFAnalyze(String topic, String key) {
        this.topic = topic;
        this.key = key;
    }

    /**
     * TODO 并发
     *
     * @param topic
     * @param UHFAnalyzeRes
     * @return
     */
    public synchronized KafkaKeySender send(String topic, String UHFAnalyzeRes) {
        sender.Send(topic, UHFAnalyzeRes);
        return sender;
    }

    public void receive() {
        receiver = KafkaReceiver.getInstance();
        receiver.Receive();

        Date strat = new Date();
        long loopTime = 0;
        while (loopTime <= 2 * 60){//2 mins
            if (receiver.topicResult != null){
                jsonStr = receiver.topicResult;
                analyzeTopic = receiver.analyzeTopic;
                break;
            }
            Date nowTime = new Date();
            loopTime = (nowTime.getTime() - strat.getTime()) / 1000;
        }
        if (receiver.topicResult != null){
            System.out.println("UHF json串接收超时，超时时间为2分钟.");
        }
    }

    public void uhfTrain() {
        try {
            System.out.println("UHF model is training");
//            String uhfPythonPath = "/usr/local/platformTest/uhfTest/uhf_train.py";
//            String uhfPythonPath = "E:\\prps_tensorflow\\Code\\uhf_train.py";
            String uhfPythonPath = "E:\\JinXiejie\\UHFUrlData\\uhf_train.py";
            String[] args = new String[]{pythonExePath, uhfPythonPath, topic, key};
            Process pr = Runtime.getRuntime().exec(args);

            BufferedReader in = new BufferedReader(new
                    InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
//            send(key, topic);
            System.out.println("UHF model trained successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            if (key.equals("train")) {
//                System.out.println("UHF model is training");
//
//                String uhfPythonPath = "E:\\prps_tensorflow\\Code\\uhf_train.py";
//                String[] args = new String[]{pythonExePath, uhfPythonPath, topic, key};
//                Process pr = Runtime.getRuntime().exec( args);
//
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(pr.getInputStream()));
//                String line;
//                while ((line = in.readLine()) != null) {
//                    System.out.println(line);
//                }
//                in.close();
//                pr.waitFor();
//                System.out.println("UHF model train successfully");
//            }
//            if (key.equals("test")){
//                System.out.println("UHF model is predicting");
//                String uhfPythonPath = "E:\\prps_tensorflow\\Code\\uhf_train.py";
//                String[] args = new String[]{pythonExePath, uhfPythonPath, topic, key};
//                Process pr = Runtime.getRuntime().exec( args);
//
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(pr.getInputStream()));
//                String line;
//                while ((line = in.readLine()) != null) {
//                    System.out.println(line);
//                }
//                in.close();
//                pr.waitFor();
//                System.out.println("UHF model predict successfully");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void uhfTest(String jsonStr) {
        try {
            System.out.println("正在调用UHF模型分析UHF数据");
            JSONObject json = JSONObject.fromObject(jsonStr);
//            String uhfPythonPath = "/usr/local/platformTest/uhfTest/uhf_analyze.py";
//            String uhfPythonPath = "E:\\prps_tensorflow\\Code\\uhf_analyze.py";
            String uhfPythonPath = "E:\\JinXiejie\\UHFUrlData\\uhf_analyze.py";
            String topic = json.getString("type");
            String urlStr = json.getString("url");
            String key = "test";
            String fileName = "uhfDat.dat";
//            String savePath = "/usr/local/platformTest/uhfTest";
//            String savePath = "E:\\prps_tensorflow\\UHFUrlData";
            String savePath = "E:\\JinXiejie\\UHFUrlData";
            String uhfAnalyzeData = double2String(uhfDataReaderDat.uhfAnalyzeReader(urlStr, fileName, savePath));
            String[] args = new String[]{pythonExePath, uhfPythonPath, topic, key, uhfAnalyzeData};
            Process pr = Runtime.getRuntime().exec(args);
            System.out.println("runtime is starting.");
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            String UHFAnalyzeRes = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                UHFAnalyzeRes = labelIntoType(line);
            }

            System.out.println("UHF数据分析结束并发送分析结果");
            System.out.println("UHF数据分析结果：" + UHFAnalyzeRes);
            send(topic, UHFAnalyzeRes);
            in.close();
            pr.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        JSONObject json = JSONObject.parseObject(jsonStr);
//        String id = json.getString("id");
//        String type = json.getString("type");
//        if (type == null || type.split("-").length < 2) {
//            System.out.println("无效数据");
//        }
//        String[] types = type.split("-");
//        String topic = types[1].trim() + "-RESULT";
//        String key = "test-" + topic;
//
//        //发送分析请求
//        send(key, jsonStr.toString());
//
//        AnalyseTask task = new AnalyseTask();
//        task.id = id;
//        receiver.addTask(task);
//        System.out.println("start wait");
//        try {
//            task.wait(30000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public String labelIntoType(String label){
        int labelInt = Integer.parseInt(label);
        if (labelInt == 1)
            return "颗粒放电";
        else if (labelInt == 2)
            return "沿面放电";
        else if (labelInt == 3)
            return "内部放电";
        else if (labelInt == 4)
            return "电晕放电";
        else if (labelInt == 0)
            return "正常";
        return "不在当前模型缺陷识别类型范围内.";
    }

    public String double2String(double[] doubleArray){
        StringBuilder stringBuilder = new StringBuilder();
        int len = doubleArray.length;
        for (int i=0;i<len - 1;i++){
            stringBuilder.append(String.valueOf(doubleArray[i]));
            stringBuilder.append(";");
        }
        stringBuilder.append(String.valueOf(doubleArray[len - 1]));
        return stringBuilder.toString();
    }
}

package com.jhcomn.lambda.mllib.uhf.preprocess;

import com.alibaba.fastjson.JSONObject;
import com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader.UHFDataReaderDat;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.AnalyseTask;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.KafkaKeySender;
import com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit.KafkaReceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import net.sf.json.JSONObject;

public class UHFAnalyze {
    private KafkaKeySender sender = KafkaKeySender.getInstance();
    public UHFDataReaderDat uhfDataReaderDat = new UHFDataReaderDat();
    private KafkaReceiver receiver = null;
//    private static String pythonExePath = "/home/jhcomn/anaconda3/bin/python";
    private static String pythonExePath = "E:\\JinXiejie\\Anaconda3\\envs\\tensorflow\\python.exe";
    private String topic = null;
    private String key = null;

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
     * @param key
     * @param topic
     * @return
     */
    public synchronized KafkaKeySender send(String key, String topic) {
        sender.Send(key, topic);
        return sender;
    }

    public void receive() {
        receiver.Receive();
    }

    public void uhfTrain() {
        try {
            System.out.println("UHF model is training");
//            String uhfPythonPath = "/usr/local/platformTest/uhfTest/uhf_train.py";
            String uhfPythonPath = "E:\\JinXiejie\\UHFuRLData\\uhf_train.py";
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
            send(key, topic);
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
            JSONObject json = JSONObject.parseObject(jsonStr);
            System.out.println("正在调用UHF模型分析UHF数据");
//            String uhfPythonPath = "/usr/local/platformTest/uhfTest/uhf_train.py";
            String uhfPythonPath = "E:\\JinXiejie\\UHFuRLData\\uhf_analyze.py";
            String topic = json.getString("type");
            String urlStr = json.getString("url");
            String key = "test";
            String fileName = "uhfDat.dat";
            String savePath = "E:\\JinXiejie\\UHFuRLData";
            String uhfAnalyzeData = double2String(uhfDataReaderDat.uhfAnalyzeReader(urlStr, fileName, savePath));
            String[] args = new String[]{pythonExePath, uhfPythonPath, topic, key, uhfAnalyzeData};
            Process pr = Runtime.getRuntime().exec(args);
            System.out.println("runtime is starting.");
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("hh");
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
            System.out.println("UHF model predict successfully");
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

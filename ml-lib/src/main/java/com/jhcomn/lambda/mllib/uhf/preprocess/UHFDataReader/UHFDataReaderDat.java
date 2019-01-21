package com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader;

import com.jhcomn.lambda.mllib.uhf.preprocess.UhfFeature.ExtractUhfFeature;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.*;
import java.nio.file.Path;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class UHFDataReaderDat {
    public String label = "";
//    public UHFDataReaderDat(String label){
//        this.label = label;
//    }

    public String prpsDataParser(String filePath) throws IOException {
        PDViewObject pvo = new PDViewObject();
        File file = new File(filePath);
        InputStream fis = new FileInputStream(file);
        pvo = pvo.parserPrps(fis);
        if (pvo.datas != null) {
            int[][] datas = pvo.datas;
            System.out.println(datas.length);
            System.out.println(datas[0].length);
            ExtractUhfFeature extractUhfFeature = new ExtractUhfFeature(pvo.datas, pvo.type);
            label = String.valueOf(pvo.type);
            String trainData = extractUhfFeature.extractFeature();
            return trainData;
        }
//        File[] lists = file.listFiles();
//        int idx = 0;
//        if (lists != null) {
//            System.out.println("正在生成PRPS训练数据。。。");
//            for (File f : lists) {
//                InputStream fis = new FileInputStream(f);
////                String destFilePathTrain = "E:/JinXiejie/data/PRPS/PDMSystemPdmSys_CouplerSPDC-Channel_2_10/prps_train" + String.valueOf(idx) + ".csv";
////                String destFilePathTrain = destFilePath + String.valueOf(idx) + ".csv";
//                pvo = pvo.parserPrps(fis);
//                if (pvo.datas != null){
//                    int[][] datas = pvo.datas;
//                    System.out.println(datas.length);
//                    System.out.println(datas[0].length);
//                    ExtractUhfFeature extractUhfFeature = new ExtractUhfFeature(pvo.datas, pvo.type);
//                    label = String.valueOf(pvo.type);
//                    String trainData = extractUhfFeature.extractFeature();
//                    return trainData;
//                }
//                idx++;
//                fis.close();
//            }
//        }
        return null;
    }

}


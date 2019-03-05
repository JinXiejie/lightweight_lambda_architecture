package com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader;

import com.jhcomn.lambda.mllib.uhf.preprocess.UhfFeature.ExtractUhfFeature;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class UHFDataReaderDat {
    public String label = "";
//    public UHFDataReaderDat(String label){
//        this.label = label;
//    }

    public double[] prpsDataParser(String filePath) throws IOException {
        PDViewObject pvo = new PDViewObject();
        File file = new File(filePath);
        InputStream fis = new FileInputStream(file);
        pvo = pvo.parserPrps(fis);
        if (pvo.datas != null) {
            int[][] datas = pvo.datas;
//            printArray(datas);
//            System.out.println(datas.length);
//            System.out.println(datas[0].length);
            ExtractUhfFeature extractUhfFeature = new ExtractUhfFeature(pvo.datas, pvo.type);
            label = String.valueOf(pvo.type);
            return extractUhfFeature.extractFeature();
        }
        else {
            System.out.println("读取UHF数据出错！");
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

    public void printArray(int[][] datas){
        if (datas != null && datas.length > 0){
            int m = datas.length;
            int n = datas[0].length;
            for (int i=0;i<m;i++){
                for (int j=0;j<n;j++){
                    System.out.print(datas[i][j]);
                }
                System.out.println();
            }
        }
    }

    public double[] uhfAnalyzeReader(String urlStr,String fileName,String savePath){
        try {
            System.out.println("uhfAnalyzeReader urlStr: " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdir();
            }
            File file = new File(saveDir+File.separator+fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if(fos!=null){
                fos.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
            System.out.println("info: " + url + " download successfully.");

            System.out.println("Extracting UHF feature and get the feature vector.");
//            String filePath = savePath + "\\" + fileName;
            String filePath = savePath + "/" + fileName;
            double[] featureVector = prpsDataParser(filePath);
            boolean isDelete = delFile(filePath);
            if (isDelete)
                return featureVector;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private boolean delFile(String filePath){
        File file = new File(filePath);
        if(file.exists() && file.isFile())
            if (file.delete())
                return true;
        return false;
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}


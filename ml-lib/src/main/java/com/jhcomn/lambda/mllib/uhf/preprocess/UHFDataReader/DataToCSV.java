package com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader;

import java.io.*;

public class DataToCSV {

    public void output(PDViewObject vo) throws IOException {

        String destFilePathTrain = "C:/Users/Administrator/Desktop/train.csv";
        PrintWriter pw = new PrintWriter(new FileWriter(destFilePathTrain));

        pw.print("version_id,");
        pw.print("devnum_id,");
        pw.print("channelnum_id,");
        pw.print("devstatus_id,");
        pw.print("datastatus_id,");
        pw.print("type_id,");
        pw.print("alarm_id,");
        pw.print("\n");
        for (int j = 0; j < vo.datas[0].length; j++) {
            pw.print(vo.version + ",");
            pw.print(vo.devnum + ",");
            pw.print(vo.channelnum + ",");
            pw.print(vo.devstatus + ",");
            pw.print(vo.datastatus + ",");
            pw.print(vo.type + ",");
            pw.print(vo.alarm + ",");
            for (int i = 0; i < vo.datas.length; i++) {
                if (i != vo.datas.length - 1)
                    pw.print(vo.datas[i][j] + ",");
                else
                    pw.print(vo.datas[i][j] + "\n");
            }
        }
        System.out.println(vo.phaseNum);
    }

    public boolean exportPRPSCsv(File file, PDViewObject vo) {
        boolean isSucess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
            bw.append("version_id").append(",");
            bw.append("devnum_id").append(",");
            bw.append("channelnum_id").append(",");
            bw.append("devstatus_id").append(",");
            bw.append("datastatus_id").append(",");
            bw.append("type_id").append(",");
            bw.append("alarm_id").append(",");
//            String phase = "phaseNum_" + String.valueOf(vo.phaseNum);
            for (int i = 0; i < vo.phaseNum; i++) {
                String phaseId = "p" + String.valueOf(i);
                if (i != vo.phaseNum - 1)
                    bw.append(phaseId).append(",");
                else
                    bw.append(phaseId).append("\r");
            }
            if (vo.datas != null) {
                for (int j = 0; j < vo.datas[0].length; j++) {
                    bw.append(String.valueOf(vo.version)).append(",");
                    bw.append(String.valueOf(vo.devnum)).append(",");
                    bw.append(String.valueOf(vo.channelnum)).append(",");
                    bw.append(String.valueOf(vo.devstatus)).append(",");
                    bw.append(String.valueOf(vo.datastatus)).append(",");
                    bw.append(String.valueOf(vo.type)).append(",");
                    bw.append(String.valueOf(vo.alarm)).append(",");

                    for (int i = 0; i < vo.datas.length; i++) {
                        String phaseNum = String.valueOf(vo.datas[i][j]);
                        if (i != vo.datas.length - 1)
                            bw.append(phaseNum).append(",");
                        else
                            bw.append(phaseNum).append("\r");
                    }
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSucess;
    }

    public boolean exportPRPDCsv(File file, PDViewObject vo) {
        boolean isSucess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
            bw.append("version_id").append(",");
            bw.append("devnum_id").append(",");
            bw.append("channelnum_id").append(",");
            bw.append("devstatus_id").append(",");
            bw.append("datastatus_id").append(",");
            bw.append("type_id").append(",");
            bw.append("alarm_id").append(",");
//            String phase = "phaseNum_" + String.valueOf(vo.phaseNum);
            for (int i = 0; i < 64; i++) {
                String phaseId = "p" + String.valueOf(i);
                if (i != 63)
                    bw.append(phaseId).append(",");
                else
                    bw.append(phaseId).append("\r");
            }
            if (vo.datas != null) {
                System.out.println(vo.phaseNum);
                for (int i = 0; i < vo.datas.length; i++) {
                    for (int j = 0; j < vo.datas[i].length; i++)
                        System.out.print(vo.datas[i][j] + " , ");
                    System.out.println();
                }
                for (int j = 0; j < vo.datas[0].length; j++) {
                    bw.append(String.valueOf(vo.version)).append(",");
                    bw.append(String.valueOf(vo.devnum)).append(",");
                    bw.append(String.valueOf(vo.channelnum)).append(",");
                    bw.append(String.valueOf(vo.devstatus)).append(",");
                    bw.append(String.valueOf(vo.datastatus)).append(",");
                    bw.append(String.valueOf(vo.type)).append(",");
                    bw.append(String.valueOf(vo.alarm)).append(",");

                    for (int i = 0; i < vo.datas.length; i++) {
                        String phaseNum = String.valueOf(vo.datas[i][j]);
                        if (i != vo.datas.length - 1)
                            bw.append(phaseNum).append(",");
                        else
                            bw.append(phaseNum).append("\r");
                    }
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSucess;
    }
}

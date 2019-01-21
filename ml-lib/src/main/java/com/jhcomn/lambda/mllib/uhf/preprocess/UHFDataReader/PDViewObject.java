package com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class PDViewObject {
    /**
     * 版本
     */
    public float version;
    /**
     * 图谱生成时间
     */
    public String datetime;
    /**
     * 被监测设备编号
     */
    public String devnum;
    /**
     * 装置地址
     */
    public String addr;
    /**
     * 通道编号
     */
    public int channelnum;
    /**
     * 被监测设备状态
     */
    public int devstatus;
    /**
     * 数据状态
     */
    public int datastatus;
    /**
     * 放电类型
     */
    public int type;
    /**
     * 报警等级
     */
    public int alarm;
    /**
     * 放电类型概率统计标志
     */
    public int probSt;
    //10 种放电类型的概率
    public float[] probs = new float[10];
    /**
     * 放电幅值标定系数
     */
    public short coeff;
    /**
     * 放电相位窗数
     */
    public int phaseNum;
    /**
     * 幅值量化值
     */
    public int avg;
    /**
     * 工频周期数
     */
    public int cycNum;
    /**
     * 放电量峰值
     */
    public float intens;
    /**
     * 放电量均值
     */
    public float avgV;
    /**
     * 50Hz相关性
     */
    public float fiftyHZ;
    /**
     * 100Hz相关性
     */
    public float hundredHZ;
    /**
     * 图谱类型标志
     */
    public int tptype;
    /**
     * 幅值单位标志
     */
    public int unit;
    /**
     * 幅值起始值
     */
    public int minvalue;
    /**
     * 幅值最大值
     */
    public int maxvalue;
    /**
     * 数据中的最大值，用来标志生成图谱的z轴（画图时候用到），图谱文件中不存在此值
     */
    public int maxint;
    /**
     * 数据,对应flash中的datavalue
     */
    public int[][] datas;

    /**
     * y轴最大坐标值
     */
    public double maxYCoordinate = 0;
    /**
     * y轴最小坐标值
     */
    public double minYCoordinate = 0;
    /**
     * x轴最小坐标值
     */
    public double maxXCoordinate = 0;
    /**
     * x轴最小坐标值
     */
    public double minXCoordinate = 0;

    /**
     * z轴最小坐标值
     */
    public double maxZCoordinate = 0;
    /**
     * z轴最小坐标值
     */
    public double minZCoordinate = 0;

    public PDViewObject parserPrps(InputStream is) throws IOException {
        PDViewObject vo = new PDViewObject();
        if (is == null) {
            System.out.println("读取错误！！！");
            return vo;
        }
        try {

            SwappedDataInputStream stream = new SwappedDataInputStream(is);

            vo.version = stream.readFloat();
            vo.datetime = readDate(stream);
            vo.devnum = ParserUtil.readString(stream, 50);
            vo.addr = readAddress(stream);//
            vo.channelnum = stream.readInt();
            vo.devstatus = stream.readByte();
            vo.datastatus = stream.readByte();
            vo.type = stream.readByte();
            vo.alarm = stream.readByte();
            vo.probSt = stream.readByte();
            //10 种放电类型的概率

            for (int i = 0; i < 10; i++) {
                vo.probs[i] = stream.readFloat();
            }
            vo.coeff = stream.readShort();
            vo.phaseNum = stream.readInt();
            vo.avg = stream.readInt();
            vo.cycNum = stream.readInt();
            vo.intens = stream.readFloat();
            vo.avgV = stream.readFloat();
            int maxint = 0;
            vo.fiftyHZ = stream.readFloat();
            vo.hundredHZ = stream.readFloat();
            vo.tptype = stream.readByte();
            vo.unit = stream.readByte();
            vo.minvalue = stream.readShort();
            vo.maxvalue = stream.readShort();

            int[][] datas = null;
            if (vo.tptype == 0) {//PRPD型图谱,phaseNum窗数
                vo.maxXCoordinate = vo.phaseNum;
                vo.maxYCoordinate = vo.avg;
                datas = new int[vo.phaseNum][vo.avg];
                //m×n的二维数组。其中：m为相位窗数，n为幅值量化值，数组值为对应的放电频次。
                //m对应vo.phaseNum,n对应vo.avg
                for (int x = 0; x < vo.phaseNum; x++) {
                    for (int y = 0; y < vo.avg; y++) {
                        int value = stream.readShort();
                        if (value != 0) {
                            maxint = Math.max(maxint, value);
                            datas[x][y] = value;
                            if (value > vo.maxZCoordinate) {
                                vo.maxZCoordinate = value;
                            }
                        }
                    }
                }
            } else if (vo.tptype == 1) {//PRPS型图谱,phaseNum窗数
                if (vo.unit == 1) {
                    vo.maxXCoordinate = vo.phaseNum;
                    vo.maxYCoordinate = vo.cycNum;
                    datas = new int[vo.phaseNum][vo.cycNum];
                    //m×L的二维数组。其中：m为相位窗数，L为工频周期数，数组值为对应的放电强度。
                    //m对应vo.phaseNum,n对应vo.cycNum
                    for (int x = 0; x < vo.phaseNum; x++) {
                        for (int y = 0; y < vo.cycNum; y++) {
                            int value = stream.readShort();
                            if (value != 0) {
                                maxint = Math.max(maxint, value);
                                datas[x][y] = value;
                                if (value > vo.maxZCoordinate) {
                                    vo.maxZCoordinate = value;
                                }
                            }
                        }
                    }
                } else if (vo.unit == 2) {
//				vo.maxXCoordinate = vo.cycNum;
//				vo.maxYCoordinate = vo.phaseNum;
//				datas = new int[vo.cycNum][vo.phaseNum];

                    vo.maxXCoordinate = vo.phaseNum;
                    vo.maxYCoordinate = vo.cycNum;
                    datas = new int[vo.phaseNum][vo.cycNum];
                    for (int x = 0; x < vo.cycNum; x++) {
                        for (int y = 0; y < vo.phaseNum; y++) {
                            int value = stream.readShort();
                            if (value != 0) {
                                maxint = Math.max(maxint, value);
                                datas[y][x] = value;
                                if (value > vo.maxZCoordinate) {
                                    vo.maxZCoordinate = value;
                                }
                            }
                        }
                    }
                }
            }
            vo.maxint = maxint;
            vo.datas = datas;


            //生成CSV格式数据，并导入destFilePathTrain，非必要
//            if (vo.tptype == 1) {//PRPS型图谱,phaseNum窗数
//                DataToCSV dataTrain = new DataToCSV();
////				String destFilePathTrain = "C:/Users/Administrator/Desktop/train.csv";
//                File file = new File(destFilePathTrain);
//                System.out.println(dataTrain.exportPRPSCsv(file, vo));
//            }


        } catch (EOFException e) {
            System.out.println("已经达到文件末尾");
        }
        return vo;
    }

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String readDate(SwappedDataInputStream stream) throws IOException {
        int t = stream.readInt();
        long ltime = ((long) (t - (8 * 60 * 60))) * 1000l;
        return df.format(new Date(ltime));
    }

    private static String readAddress(SwappedDataInputStream stream) throws IOException {
        StringBuffer addr = new StringBuffer();

        for (int i = 0; i < 4; i++) {
            int b = (int) stream.readByte();
            if (b < 0) {
                b += 256;
            }
            addr.append(b);
            if (i < 3) {
                addr.append(".");
            }
        }
        return addr.toString();
    }
}

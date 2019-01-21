package com.jhcomn.lambda.mllib.hfct1500;

import org.apache.hadoop.fs.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by immig on 2017/5/23.
 */
public class Hfct1500PrpdReader {

    private byte[] prpdData;

    public Hfct1500PrpdReader() {
    }

    public Hfct1500PrpdReader(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] prpdData = new byte[fis.available()];
        fis.read(prpdData, 0, prpdData.length);
        this.prpdData = prpdData;
    }
    public Hfct1500PrpdReader(byte[] prpdData){
        this.prpdData = prpdData;
    }

    /**
     * 读取本地文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public double[][] readLocal(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] prpdData = new byte[fis.available()];
        fis.read(prpdData, 0, prpdData.length);
        this.prpdData = prpdData;
        return read();
    }

    /**
     * 读取HDFS文件
     * @param hdfsPath
     * @param fileSystem
     * @return
     * @throws IOException
     */
    public double[][] readHDFS(String hdfsPath, FileSystem fileSystem) throws IOException {
        if (fileSystem == null)
            return null;
        FSDataInputStream fsis = new FSDataInputStream(fileSystem.open(new Path(hdfsPath)));
        byte[] prpdData = new byte[fsis.available()];
        fsis.read(prpdData, 0, prpdData.length);
        this.prpdData = prpdData;
        return read();
    }

    public double[][] read(){
        double[][] ret = null;

        ByteBuffer buffer = ByteBuffer.wrap(prpdData);

        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int length = buffer.getInt(); //prpd点数

        ret = new double[length][3];

        for(int i = 0; i < length; i++){ //读取相位
            ret[i][0] = buffer.getFloat();
        }

        for(int i = 0; i < length; i++){ //读取幅值
            ret[i][1] = buffer.getFloat();
        }

        for(int i = 0; i < length; i++){
            ret[i][2] = buffer.getFloat();
        }

        return ret;
    }

    public static void main(String[] args) throws IOException {
        Hfct1500PrpdReader reader = new Hfct1500PrpdReader("D:/1.prpd");
        double[][] data = reader.read();
        System.out.println(data);
    }

}

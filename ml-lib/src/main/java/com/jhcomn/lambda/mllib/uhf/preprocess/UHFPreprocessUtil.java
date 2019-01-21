package com.jhcomn.lambda.mllib.uhf.preprocess;

import com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader.UHFDataReaderDat;
import com.jhcomn.lambda.mllib.uhf.preprocess.audio.AudioPreProcess;
import com.jhcomn.lambda.mllib.uhf.preprocess.audio.FeatureExtract;
import com.jhcomn.lambda.mllib.uhf.preprocess.audio.WaveData;
import com.jhcomn.lambda.mllib.uhf.preprocess.feature.FeatureVector;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkContext;

import java.io.IOException;

/**
 * UHF在线平台预处理
 * Created by jinxj on 2019/1/5.
 */
public class UHFPreprocessUtil extends UHFLocalPreprocessUtil {

    private SparkContext sc = null;
    private FileSystem fs = null;

    public String label = "";

    public UHFPreprocessUtil(SparkContext sc, FileSystem fs) {
        this.sc = sc;
        this.fs = fs;
    }

    public UHFPreprocessUtil() {
    }

    /**
     * xgb格式的特征提取
     * @param srcPath
     * @param tag
     * @return
     * @throws IOException
     */
    public String generateFeatures(String srcPath, String tag) throws IOException {
//        if (fs == null || srcPath == null)
        if (srcPath == null)
            return null;
        Path path = new Path(srcPath);
        UHFDataReaderDat uhfDataReaderDat = new UHFDataReaderDat();
        label = uhfDataReaderDat.label;
        return uhfDataReaderDat.prpsDataParser(srcPath);
//        return transFeature2XGB(getFeature(path), tag);
    }

    @Override
    protected String transFeature2XGB(double[] feature, String tag) {
        if (feature == null)
            return null;
        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < feature.length; i++) {
//            String f = "" + (i + 1) + ":" + feature[i];
//            if (i < feature.length - 1)
//                sb.append(f + " ");
//            else
//                sb.append(f);
//        }
        for (int i = 0; i < feature.length; i++) {
            if (i < feature.length - 1)
                sb.append(feature[i] + " ");
            else
                sb.append(feature[i]);
        }
        return sb.toString();
    }

    /**
     * 提取整个文件的特征数据
     * @param filePath
     * @return
     */
    protected double[] getFeature(Path filePath) {
        //获取文件所有帧的mfcc特征向量
        FeatureVector featureVector = extractFeatureFromHDFS(filePath);
        if (featureVector == null) {
            return null;
        }
        int frameNum = featureVector.getNoOfFrames();
        //行：帧 + 列：特征
        double[][] allFeatures = new double[frameNum][MFCC_FEATURE_DIMENSION];
        for (int i = 0; i < frameNum; i++) {
            allFeatures[i] = featureVector.getFeatureVector()[i];
        }
        //计算整个文件的mfcc特征--平均值(所有帧)
        double[] avgFeatures = new double[MFCC_FEATURE_DIMENSION];
        for (int i = 0; i < MFCC_FEATURE_DIMENSION; i++) {
            double tmp = 0.0d;
            for (int j = 0; j < frameNum; j++) {
                tmp += allFeatures[j][i];
            }
            double value = tmp / frameNum;
            if (value == Double.NaN)
                avgFeatures[i] = 0;
            else
                avgFeatures[i] = tmp / frameNum;
        }

        //添加时域+频域特征
        if (tdFeature == null || fdFeature == null) {
            System.out.println("TDFeature or FDFeature is null...return NULL feature...");
            return null;
        }

        double[] tds = tdFeature.getTDFeature();
        double[] fds = fdFeature.getFDFeatures();
        double[] retFeatures = new double[MFCC_FEATURE_DIMENSION + tdFeature.TD_FEATURE_DIMENSION + fdFeature.FD_FEATURE_DIMENSION];
        //copy mfcc
        System.arraycopy(avgFeatures, 0, retFeatures, 0, avgFeatures.length);
        //copy tds
        int index = 0;
        for (int i = MFCC_FEATURE_DIMENSION, j = 0;
             i < retFeatures.length && j < tdFeature.TD_FEATURE_DIMENSION;
             i++, j++)
        {
            if (tds[j] == Double.NaN)
                tds[j] = 0;
            retFeatures[i] = tds[j];
            index = i;
        }
        tdFeature.clearAll();
        //copy fds
        for (int i = index + 1, j = 0;
             i < retFeatures.length && j < fdFeature.FD_FEATURE_DIMENSION;
             i++, j++)
        {
            if (fds[j] == Double.NaN)
                fds[j] = 0;
            retFeatures[i] = fds[j];
        }
        fdFeature.clearAll();
        return retFeatures;

    }

    protected FeatureVector extractFeatureFromHDFS (Path path) {
//        float[] amplitudes;
//        waveData = new WaveData();
//        amplitudes = waveData.extractAmplitudeFromHDFS(this.fs, path);
//        if (amplitudes == null) {
//            return null;
//        }
//        audioPreProcess = new AudioPreProcess(amplitudes, SAMPLE_PER_FRAME, SAMPLING_RATE, this);
//        featureExtract = new FeatureExtract(audioPreProcess.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
//        featureExtract.makeMfccFeatureVector();
//        return featureExtract.getFeatureVector();
        return null;
    }
}

package com.jhcomn.lambda.mllib.uw1000.preprocess;

import com.jhcomn.lambda.mllib.uw1000.preprocess.audio.AudioPreProcess;
import com.jhcomn.lambda.mllib.uw1000.preprocess.audio.FeatureExtract;
import com.jhcomn.lambda.mllib.uw1000.preprocess.audio.WaveData;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.FDFeature;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.FeatureVector;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.IFeatureCallback;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.TDFeature;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.pca.Matrix;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.pca.PCA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * uw1000超声波wav音频文件预处理&特征提取
 * Created by shimn on 2017/10/23.
 */
public class UW1000LocalPreprocessUtil implements IFeatureCallback {

    protected static final int SAMPLING_RATE = 8000; //
    protected static final int SAMPLE_PER_FRAME = 256; // 512,23.22ms
    protected static final int MFCC_FEATURE_DIMENSION = 12;
    protected FeatureExtract featureExtract;
    protected WaveData waveData;
    protected AudioPreProcess audioPreProcess;

    //时域特征
    protected TDFeature tdFeature = null;
    //频域特征
    protected FDFeature fdFeature = null;

    /**
     * local file version
     */

    /**
     * 获取svm格式的音频特征，并输出到文件
     * @param src
     * @param desc
     * @param tag
     * @return nums 返回该类数据总数
     */
    public int generateFeaturesIris(String src, String desc, String tag) {
        BufferedWriter bw = null;
        int nums = 0;
        try {
            bw = new BufferedWriter(new FileWriter(new File(desc)));
            File srcFile = new File(src);
            // src为文件夹
            if (srcFile.isDirectory()) {
                for (File f : srcFile.listFiles()) {
                    System.out.println(f.getPath());    //test
                    double[] feature = getFeature(f.getPath());
                    if (feature == null) {
                        bw.write(f.getPath());
                        System.out.println("fail");
                    }
                    else {
                        String line = transFeature2IrisStr(feature, tag);
//                        System.out.println(line);   //test
                        bw.write(line);
                    }
                    nums++;
                    bw.newLine();
                }
            }
            // src为单一文件
            else {
                System.out.println(srcFile.getPath());
                bw.write(transFeature2IrisStr(getFeature(srcFile.getPath()), tag));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nums;
    }

    /**
     * 获取xgb格式的音频特征，并输出到文件
     * @param src
     * @param desc
     * @param tag
     * @return
     */
    public int generateFeaturesXGB (String src, String desc, String tag) {
        BufferedWriter bw = null;
        int nums = 0;
        try {
            bw = new BufferedWriter(new FileWriter(new File(desc)));
            File srcFile = new File(src);
            // src为文件夹
            if (srcFile.isDirectory()) {
                for (File f : srcFile.listFiles()) {
                    System.out.println(f.getPath());    //test
                    double[] feature = getFeature(f.getPath());
                    if (feature == null) {
                        bw.write(f.getPath());
                        System.out.println("fail");
                    }
                    else {
                        String line = transFeature2XGB(feature, tag);
//                        System.out.println(line);   //test
                        bw.write(line);
                    }
                    nums++;
                    bw.newLine();
                }
            }
            // src为单一文件
            else {
                System.out.println(srcFile.getPath());
                bw.write(transFeature2XGB(getFeature(srcFile.getPath()), tag));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nums;
    }

    /**
     * 获取iris格式的mfcc音频特征，并输出到文件
     * @param src
     * @param desc
     * @param tag
     * @return nums 返回该类数据总数
     */
    public int generateFeaturesIrisWithPCA (String src, String desc, String tag) {
        BufferedWriter bw = null;
        PCA PCA = new PCA();
        int nums = 0;
        try {
            bw = new BufferedWriter(new FileWriter(new File(desc)));
            File srcFile = new File(src);
            // src为文件夹
            if (srcFile.isDirectory()) {
                File[] flist = srcFile.listFiles();
                double[][] features = new double[flist.length][];
                for (int i = 0; i < flist.length; i++) {
                    File f = flist[i];
                    System.out.println(f.getPath());    //test
                    double[] feature = getFeature(f.getPath());
                    if (feature != null)
                        features[i] = feature;
                }
                //pca
                double[][] fPCA = PCA.principalComponentAnalysisTraining(Matrix.transpose(features), 3); //pca维数
                fPCA = Matrix.transpose(fPCA);
                for (int j = 0; j < fPCA.length; j++) {
                    bw.write(transFeature2IrisStr(fPCA[j], tag));
                    bw.newLine();
                    nums++;
                }
            }
            // src为单一文件
            else {
                //TODO
                System.out.println(srcFile.getPath());
//                bw.write(transFeature2IrisStr(getFeature(srcFile.getPath()), tag));
//                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nums;
    }

    /**
     * 获取iris格式的mfcc音频特征，并输出到文件
     * @param trainSrc
     * @param trainDesc
     * @param trainTag
     * @return nums 返回该类数据总数
     */
    public int generateAllFeaturesIrisWithPCA (String trainSrc, String trainDesc, String trainTag,
                                               String testSrc, String testDesc, String testTag) {
        PCA PCA = new PCA();
        BufferedWriter bwTrain = null;
        BufferedWriter bwTest = null;
        int nums = 0;
        try {
            bwTrain = new BufferedWriter(new FileWriter(new File(trainDesc)));
            File trainSrcFile = new File(trainSrc);
            File[] trainFlist = trainSrcFile.listFiles();

            bwTest = new BufferedWriter(new FileWriter(new File(testDesc)));
            File testSrcFile = new File(testSrc);
            File[] testFlist = testSrcFile.listFiles();

            int fileLen = trainFlist.length + testFlist.length;
            double[][] features = new double[fileLen][];
            int index = 0;
            //
            for (int i = 0; i < trainFlist.length; i++) {
                File f = trainFlist[i];
                System.out.println(f.getPath());    //test
                double[] feature = getFeature(f.getPath());
                if (feature != null) {
                    features[i] = feature;
                    index++;
                }
            }
            //
            for (int i = 0; i < testFlist.length; i++) {
                File f = testFlist[i];
                System.out.println(f.getPath());    //test
                double[] feature = getFeature(f.getPath());
                if (feature != null)
                    features[i + index] = feature;
            }
            //pca
            double[][] fPCA = PCA.principalComponentAnalysisTraining(Matrix.transpose(features), 10); //pca维数
            fPCA = Matrix.transpose(fPCA);
            if (fPCA.length != fileLen) {
                System.out.println("pca length = " + fPCA.length + ", fileLen = " + fileLen);
            }
            int rowNum = 0;
            for (int j = 0; j < trainFlist.length && j < fPCA.length; j++) {
                bwTrain.write(transFeature2IrisStr(fPCA[j], trainTag));
                bwTrain.newLine();
                nums++;
                rowNum++;
            }
            for (int j = rowNum; j < fPCA.length && (j - rowNum) < testFlist.length; j++) {
                bwTrain.write(transFeature2IrisStr(fPCA[j], testTag));
                bwTrain.newLine();
                nums++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bwTrain != null) {
                try {
                    bwTrain.flush();
                    bwTrain.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bwTest != null) {
                try {
                    bwTest.flush();
                    bwTest.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nums;
    }

    /**
     * 将特征数据转换成iris格式，针对libsvm
     */
    private String transFeature2IrisStr(double[] feature, String tag) {
        StringBuffer sb = new StringBuffer();
        for (double f : feature) {
            sb.append(f + "").append(",");
        }
        sb.append(tag);
        return sb.toString();
    }

    /**
     * 特征转换为Xgboost格式
     * @param feature
     * @param tag
     * @return
     */
    protected String transFeature2XGB(double[] feature, String tag) {
        StringBuffer sb = new StringBuffer();
        sb.append(tag + " ");
        for (int i = 0; i < feature.length; i++) {
            String f = "" + (i + 1) + ":" + feature[i];
            if (i < feature.length - 1)
                sb.append(f + " ");
            else
                sb.append(f);
        }
        return sb.toString();
    }

    /**
     * 提取整个文件的特征数据
     * @param fileName
     * @return
     */
    protected double[] getFeature(String fileName) {
        //获取文件所有帧的mfcc特征向量
        FeatureVector featureVector = extractFeatureFromFile(new File(fileName));
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
            avgFeatures[i] = tmp / frameNum;
        }

        //添加时域+频域特征
        if (tdFeature == null || fdFeature == null)
            throw new RuntimeException("TDFeature or FDFeature is null");

        double[] tds = tdFeature.getTDFeature();
        double[] fds = fdFeature.getFDFeatures();
        double[] retFeatures = new double[MFCC_FEATURE_DIMENSION + tdFeature.TD_FEATURE_DIMENSION + fdFeature.FD_FEATURE_DIMENSION];
        //copy mfcc
        System.arraycopy(avgFeatures, 0, retFeatures, 0, avgFeatures.length);
        //copy tds
        int index = 0;
        for (int i = MFCC_FEATURE_DIMENSION, j = 0;
             i < retFeatures.length && j < tdFeature.TD_FEATURE_DIMENSION;
             i++, j++) {
            retFeatures[i] = tds[j];
            index = i;
        }
        tdFeature.clearAll();
        //copy fds
        for (int i = index + 1, j = 0;
                i < retFeatures.length && j < fdFeature.FD_FEATURE_DIMENSION;
                i++, j++) {
            retFeatures[i] = fds[j];
        }
        fdFeature.clearAll();
        return retFeatures;

    }

    /**
     * 处理源文件，提取mfcc特征向量(提取整个文件每一帧的特征)
     * @param file
     * @return
     */
    protected FeatureVector extractFeatureFromFile(File file) {
        float[] amplitudes;
        waveData = new WaveData();
        amplitudes = waveData.extractAmplitudeFromFile(file);
        if (amplitudes == null) {
            return null;
        }

        audioPreProcess = new AudioPreProcess(amplitudes, SAMPLE_PER_FRAME, SAMPLING_RATE, this);
        featureExtract = new FeatureExtract(audioPreProcess.framedSignal, SAMPLING_RATE, SAMPLE_PER_FRAME);
        featureExtract.makeMfccFeatureVector();
        return featureExtract.getFeatureVector();
    }

    @Override
    public void onTDFeatureCallback(TDFeature td) {
        this.tdFeature = td;
    }

    @Override
    public void onFDFeatureCallback(FDFeature fd) {
        this.fdFeature = fd;
    }
}

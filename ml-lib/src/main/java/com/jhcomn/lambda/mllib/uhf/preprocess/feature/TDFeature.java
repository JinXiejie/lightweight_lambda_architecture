package com.jhcomn.lambda.mllib.uhf.preprocess.feature;

/**
 * 时域特征
 * Created by shimn on 2017/11/6.
 */
public class TDFeature {

    //时域特征维数
    public static final int TD_FEATURE_DIMENSION = 7;

    //段内帧数
    private static final int FRAME_PER_SEGMENT = 128;

    //基于帧的特征
    //过零率 generateTDFeaturesRetZCR feature = average of every frame zcr
    private double zcrFeature = 0;
    //短时平均能量 short-time energy STE
    private double steFeature = 0;
    //均方根 root mean square RMS
    private double rmsFeature = 0;
    //方差 VAR
    private double varFeature = 0;
    //绝对积分平均值 AVA
    private double avaFeature = 0;
    //峰度 BK
    private double bkFeature = 0;
    //偏度 BS
    private double bsFeature = 0;

    //基于段的特征
    private int segmentLen = 0;
    private int segmentNum = 0;
    //HZCRR feature = average of every segment hzcrr
    private double hzcrrFeature = 0;

    /**
     * 单例获取
     */
    private volatile static TDFeature td = null;
    public synchronized static TDFeature newInstance() {
        if (td == null)
            td = new TDFeature();
        return td;
    }

    /**
     * 生成基于帧的时域特征
     * @param frameSignal   归一化后的每一帧信号幅值
     * @param delta 过零阈值
     * @return  过零率
     */
    public float[][] generateTDFeaturesRetZCR(float[][] frameSignal, float delta) {
        if (frameSignal == null)
            throw new RuntimeException("[TDFeature] generateTDFeaturesRetZCR frame signal is null...");

        if (delta <= 0f)
            delta = 0.02f;
        //generateTDFeaturesRetZCR
        float[] ZCR = new float[frameSignal.length];
        segmentLen = FRAME_PER_SEGMENT;
        if (ZCR.length <= segmentLen)
            segmentLen = ZCR.length;
        segmentNum = (int) Math.ceil(ZCR.length * 1.0f / segmentLen);
        float[] avrZCRofSegment = new float[segmentNum];
        //HZCRR
        int hzcrrTempSum = 0;
        //STE
        double[] STE = new double[frameSignal.length];
        //VAR AVG
        float[] varAvgOfFrame = new float[frameSignal.length];

        for (int i = 0; i < frameSignal.length; i++) {
            float[] frame = frameSignal[i];
            //hzcrr
            if (i != 0 && i % segmentLen == 0) {
                avrZCRofSegment[(i - 1) / segmentLen] = hzcrrTempSum * 1.0f / segmentLen;
                hzcrrTempSum = 0;
            }
            //ava
            double avaSum = 0;
            for (int j = 0; j < frame.length - 1; j++) {
                if (frame[j] * frame[j+1] < 0 && Math.abs(frame[j] - frame[j+1]) > delta) {
                    //计入过零率
                    ZCR[i] += 1;
                }
                //STE
                STE[i] += Math.pow(frame[j], 2);
                //VAR AVG
                varAvgOfFrame[i] += frame[j];
                //AVA
                avaSum += Math.abs(frame[j]);
            }
            //zcr feature
            zcrFeature += ZCR[i];
            //hzcrr
            hzcrrTempSum += ZCR[i];
            //STE
            steFeature += STE[i];
            //RMS
            rmsFeature += Math.sqrt(STE[i] / frame.length);
            //VAR AVG 计算每帧幅值的均值
            varAvgOfFrame[i] /= frame.length;
            //AVA AVG 累加每帧绝对积分平均值
            avaFeature += (avaSum / frame.length);
        }
        int last = 0;
        if ((last = segmentLen * segmentNum - frameSignal.length) > 0) {
            avrZCRofSegment[segmentNum - 1] = hzcrrTempSum * 1.0f / last;
        }
        //zcr feature
        zcrFeature = zcrFeature * 1.0f / ZCR.length;
        //ste feature
        steFeature /= STE.length;
        //rms feature
        rmsFeature /= STE.length;
        //var feature
        varFeature = generateExtentFeatures(frameSignal, varAvgOfFrame);
        //ava feature
        avaFeature /= frameSignal.length;

        //result
        //返回二维数组，ret[0]为ZCR数组，ret[1]为每段的zcr均值
        float[][] ret = new float[2][];
        ret[0] = ZCR;
        ret[1] = avrZCRofSegment;
        return ret;
    }

    /**
     * 生成方差Var、峰度BK、偏度BS特征
     * @param frameSignal
     * @param varAvgOfFrame
     * @return 返回方差
     */
    private double generateExtentFeatures (float[][] frameSignal, float[] varAvgOfFrame) {
        if (frameSignal == null || varAvgOfFrame == null)
            throw new RuntimeException("[TDFeature] generateExtentFeatures frame signal or varAvgOfFrame is null...");

        //方差计算
        for (int i = 0; i < frameSignal.length; i++) {
            float[] frame = frameSignal[i];
            float avg = varAvgOfFrame[i];
            double sum = 0;
            for (int j = 0; j < frame.length; j++) {
                double tmp = frame[j] - avg;
                sum += tmp * tmp;
            }
            varFeature += (sum / frame.length);
        }
        varFeature /= frameSignal.length;
        //sd 标准差
        double sd = Math.sqrt(varFeature);
        //峰度、偏度计算
        for (int i = 0; i < frameSignal.length; i++) {
            float[] frame = frameSignal[i];
            float avg = varAvgOfFrame[i];
            double bkSum = 0, bsSum = 0;
            for (int j = 0; j < frame.length; j++) {
                double temp = Math.pow((frame[j] - avg), 3);
                bsSum += temp;
                bkSum += ((frame[j] - avg) * temp);
            }
            int N = frame.length;
            bkFeature += bkSum / ((N - 1) * varFeature * varFeature);
            bsFeature += (bsSum * N) / ((N - 1) * (N - 2) * varFeature * sd);
        }
        //BK feature average of all frames
        bkFeature /= frameSignal.length;
        //BS feature average of all frames
        bsFeature /= frameSignal.length;

        return varFeature;
    }

    /**
     * 基于段的特征，高过零率比HZCRR
     * @param ZCRR
     * @return
     */
    public float[] HZCRR(float[][] ZCRR) {
        float[] ZCR = ZCRR[0];
        float[] avr = ZCRR[1];
        if (avr.length != 0 && avr.length != segmentNum)
            segmentNum = avr.length;

        float[] HZCRR = new float[segmentNum];
        for (int i = 0; i < segmentNum; i++) {
            int margin = i * segmentLen;
            float average = 1.2f * avr[i];
            for (int j = margin; j < segmentLen + margin && j < ZCR.length; j++) {
                if (Math.abs(ZCR[j] - average) > 0)
                    HZCRR[i]++;
            }
            if (i == segmentNum - 1 && segmentLen * segmentNum > ZCR.length) {
                HZCRR[i] = HZCRR[i] * 1.0f / Math.abs(segmentLen * segmentNum - ZCR.length);
            }
            else {
                HZCRR[i] = HZCRR[i] * 1.0f / segmentLen;
            }
            //hzcrr feature
            hzcrrFeature += HZCRR[i];
        }

        //hzcrr feature average
        hzcrrFeature = hzcrrFeature / HZCRR.length;
        return HZCRR;
    }

    public double getZcrFeature() {
        return zcrFeature;
    }

    public double getHzcrrFeature() {
        return hzcrrFeature;
    }

    public double getSteFeature() {
        return steFeature;
    }

    public double getRmsFeature() {
        return rmsFeature;
    }

    public double getVarFeature() {
        return varFeature;
    }

    public double getAvaFeature() {
        return avaFeature;
    }

    public double getBkFeature() {
        return bkFeature;
    }

    public double getBsFeature() {
        return bsFeature;
    }

    private double isNAN(double value) {
        double ret = value;
        Double val = new Double(ret);
        if (val.isNaN())
            ret = 0.0;
        return ret;
    }

    public double[] getTDFeature() {
        double[] features = new double[TD_FEATURE_DIMENSION];
        //校验
        hzcrrFeature = isNAN(hzcrrFeature);
        steFeature = isNAN(steFeature);
        rmsFeature = isNAN(rmsFeature);
        varFeature = isNAN(varFeature);
        bkFeature = isNAN(bkFeature);
        bsFeature = isNAN(bsFeature);
        avaFeature = isNAN(avaFeature);
        //填充
//        features[0] = zcrFeature;
        features[0] = hzcrrFeature;
        features[1] = steFeature;
        features[2] = rmsFeature;
        features[3] = varFeature;
        features[4] = bkFeature;
        features[5] = bsFeature;
        features[6] = avaFeature;
        return features;
    }

    /**
     * 清空当前所有特征
     */
    public void clearAll() {
        zcrFeature = 0;
        steFeature = 0;
        rmsFeature = 0;
        varFeature = 0;
        avaFeature = 0;
        bkFeature = 0;
        bsFeature = 0;

        segmentLen = 0;
        segmentNum = 0;
        hzcrrFeature = 0;
    }

//    public static void main(String[] args) {
//        float[][] signal = new float[520][256];
//        Random random = new Random();
//        for (int i = 0; i < 520; i++)
//            for (int j = 0; j < 256; j++) {
//                signal[i][j] = (random.nextBoolean() == true ? 1 : -1) * random.nextFloat();
//            }
//        TDFeature td = new TDFeature();
//        td.HZCRR(td.generateTDFeaturesRetZCR(signal, 0.02f));
//        System.out.println("zcr = " + td.getZcrFeature());
//        System.out.println("hzcrr = " + td.getHzcrrFeature());
//    }
}

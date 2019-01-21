package com.jhcomn.lambda.mllib.uhf.preprocess.feature;

/**
 * 频域特征
 * Created by shimn on 2017/11/8.
 */
public class FDFeature {

    //频域特征维数
    public static final int FD_FEATURE_DIMENSION = 3;

    //功率谱最大值 MPS
    private double mpsFeature = 0;
    //中值频率 MF
    private double mfFeature = 0;
    //平均功率频率 MPF
    private double mpfFeature = 0;

    //fft
    private MyFFT fft;

    /**
     * 单例获取
     */
    private volatile static FDFeature fd = null;
    public synchronized static FDFeature newInstance() {
        if (fd == null)
            fd = new FDFeature();
        return fd;
    }
    public FDFeature() {
        fft = new MyFFT();
    }

    /**
     * 计算功率谱密度
     * @param frameSignal
     * @param samplePerFrame
     * @return
     */
    public double[] generatePSD(float[][] frameSignal, int samplePerFrame) {
        if (frameSignal == null)
            throw new RuntimeException("[FDFeature] generatePSD frame signal is null...");

        int numOfFrame = frameSignal.length;
        double[] psd = new double[numOfFrame];
        for (int i = 0; i < numOfFrame; i++) {
            float[] frame = frameSignal[i];
            int N = frame.length;
            double psdSum = 0;
            // calculate FFT for current frame
            fft.computeFFT(frame);
            // calculate magnitude spectrum
            for (int j = 0; j < N; j++) {
                double psdCell = (fft.real[j] * fft.real[j] + fft.imag[j] * fft.imag[j]) / (j + 1);
                psdSum += psdCell;
                //calculate MPS
                if (psdCell > mpsFeature)
                    mpsFeature = psdCell;
            }
            // calculate psd for current frame, use average
            psd[i] = psdSum / N;
        }
        //normalize
        mpsFeature /= 1000;
        return psd;
    }

    /**
     * 计算MF和MPF
     * @param psd
     */
    public void generateExtentFeatures (double[] psd) {
        if (psd == null)
            throw new RuntimeException("[FDFeature] generateExtentFeatures psd is null...");
        int numOfFrame = psd.length;
        double sum = 0, sumWithN = 0;
        for (int i = 0; i < numOfFrame; i++) {
            sum += psd[i];
            sumWithN += i * psd[i];
        }
        //calculate MF
        mfFeature = (sum / 2) / 1000;   //normalize 1000
        //calculate MPF
        mpfFeature = (sumWithN / sum) / 1000;   //normalize 1000
    }

    public double getMpsFeature() {
        return mpsFeature;
    }

    public double getMfFeature() {
        return mfFeature;
    }

    public double getMpfFeature() {
        return mpfFeature;
    }

    private double isNAN(double value) {
        double ret = value;
        Double val = new Double(ret);
        if (val.isNaN())
            ret = 0.0;
        return ret;
    }

    public double[] getFDFeatures() {
        double[] fds = new double[FD_FEATURE_DIMENSION];
        //校验
        mpsFeature = isNAN(mpsFeature);
        mfFeature = isNAN(mfFeature);
        mpfFeature = isNAN(mpfFeature);
        //填充
        fds[0] = mpsFeature;
        fds[1] = mfFeature;
        fds[2] = mpfFeature;
        return fds;
    }

    public void clearAll() {
        mpsFeature = 0;
        mfFeature = 0;
        mpfFeature = 0;
    }
}

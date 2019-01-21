package com.jhcomn.lambda.mllib.uw1000.preprocess.feature;

/**
 * 解决fft引用影响特征提取
 * Created by shimn on 2017/11/8.
 */
public class MyFFT extends FFT {
    @Override
    public void computeFFT(float[] signal) {
        numPoints = signal.length;
        // initialize real & imag array
        real = new float[numPoints];
        imag = new float[numPoints];
        // move the N point signal into the real part of the complex DFT's time
        // array copy, not reference
        System.arraycopy(signal, 0, real, 0, numPoints);
        // set all of the samples in the imaginary part to zero
        for (int i = 0; i < imag.length; i++) {
            imag[i] = 0;
        }
        // perform FFT using the real & imag array
        FFTRun();
    }
}

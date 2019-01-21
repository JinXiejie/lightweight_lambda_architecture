package com.jhcomn.lambda.mllib.uw1000.preprocess.audio.denoise;

/**
 * 降噪处理工厂
 * Created by shimn on 2017/11/9.
 */
public class DeNoiser {

    /**
     * 小波降噪
     */
    private volatile static Wavelet wavelet = null;
    public synchronized static Wavelet createWaveletDenoiser() {
        if (wavelet == null)
            wavelet = new Wavelet();
        return wavelet;
    }

}

package com.jhcomn.lambda.mllib.uhf.preprocess.model;

import com.jhcomn.lambda.packages.IPackage;

/**
 * Created by shimn on 2017/10/23.
 * 音频帧
 */
public class AudioFrame implements IPackage {
    double[] frame;
    double ste; //每一帧的短时能量
    int zcr;    //每一帧的过零率
    boolean vad;    //判断此帧是否有效
    int next;   //下一帧

    public double[] getFrame() {
        return frame;
    }

    public void setFrame(double[] frame) {
        this.frame = frame;
    }

    public double getSte() {
        return ste;
    }

    public void setSte(double ste) {
        this.ste = ste;
    }

    public int getZcr() {
        return zcr;
    }

    public void setZcr(int zcr) {
        this.zcr = zcr;
    }

    public boolean isVad() {
        return vad;
    }

    public void setVad(boolean vad) {
        this.vad = vad;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}

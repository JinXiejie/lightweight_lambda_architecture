package com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit;

/**
 * Created by jinxiejie on 2017/5/19.
 */
public interface IResultCallback {

    void onError(String error);

    void onFinished(AnalyseResult pkg);

}

package com.jhcomn.lambda.mllib.uhf.preprocess.feature;

/**
 * 特征提取回调接口
 * Created by shimn on 2017/11/6.
 */
public interface IFeatureCallback {

    //时域特征回调
    void onTDFeatureCallback(TDFeature td);

    //频域特征回调
    void onFDFeatureCallback(FDFeature fd);

}

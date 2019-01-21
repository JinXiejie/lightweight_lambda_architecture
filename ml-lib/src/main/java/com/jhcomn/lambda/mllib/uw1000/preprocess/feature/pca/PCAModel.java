package com.jhcomn.lambda.mllib.uw1000.preprocess.feature.pca;

import com.jhcomn.lambda.packages.IPackage;

/**
 * Created by shimn on 2017/11/14.
 */
public class PCAModel implements IPackage {

    //主成分数目
    public int numComponents;

    //训练集均值-->用于归一化
    public double[] means;

    //特征矩阵-->用于投影
    public double[][] featureVector;

    public int getNumComponents() {
        return numComponents;
    }

    public void setNumComponents(int numComponents) {
        this.numComponents = numComponents;
    }

    public double[] getMeans() {
        return means;
    }

    public void setMeans(double[] means) {
        this.means = means;
    }

    public double[][] getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(double[][] featureVector) {
        this.featureVector = featureVector;
    }
}

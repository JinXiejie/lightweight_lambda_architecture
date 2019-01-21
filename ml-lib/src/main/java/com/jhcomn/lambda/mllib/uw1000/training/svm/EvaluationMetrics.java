package com.jhcomn.lambda.mllib.uw1000.training.svm;

import com.jhcomn.lambda.packages.IPackage;

/**
 * To change this template use File | Settings | File Templates.
 */
public class EvaluationMetrics implements IPackage {

    private final int correctlyClassified;
    private final int incorrectlyClassified;

    public EvaluationMetrics(int correctlyClassified, int incorrectlyClassified) {
        this.correctlyClassified = correctlyClassified;
        this.incorrectlyClassified = incorrectlyClassified;
    }

    public int getCorrectlyClassified() {
        return correctlyClassified;
    }

    public int getIncorrectlyClassified() {
        return incorrectlyClassified;
    }
}
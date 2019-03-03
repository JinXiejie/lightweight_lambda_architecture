package com.jhcomn.lambda.mllib.uhf.preprocess.UhfFeature;

public class ExtractUhfFeature {
    private static final int UHFFeatureCnt = 80;
    private static double EPSILON = 0.0001;
    private int[][] data = null;
    private int label = -1;
    public ExtractUhfFeature(int[][] prpsData, int label){
        this.data = prpsData;
        this.label = label;
    }

    public double[] extractFeature(){
        int phaseLength = data.length;
        int cycleNums = data[0].length;

        int idx = 0;
        double[] featureVector = new double[UHFFeatureCnt];
        getLabelEncoder();
        featureVector[idx++] = label;
        featureVector[idx++] = 0;
//        for (int j=0;j<cycleNums;j++){
//            int uhfDischargeAvg = 0;
//            for (int i=0;i<phaseLength;i++){
//                uhfDischargeAvg += data[i][j];
//            }
//            System.out.println(idx);
//            featureVector[idx++] = (double)uhfDischargeAvg / (double)cycleNums;
//        }

        int posiPhaseWidth = 0;
        int negPhaseWidth = 0;

        for (int i=0;i<phaseLength;i++){
            double phaseDischargeAvg = 0.0;
            for (int j=0;j<cycleNums;j++){
                phaseDischargeAvg += data[i][j];
            }
            featureVector[idx++] = phaseDischargeAvg / (double)cycleNums;
            if (phaseDischargeAvg > 0.1){
                if (i < 32)
                    posiPhaseWidth += 1;
                else
                    negPhaseWidth += 1;
            }
        }



        double dischargePhaseAvg = 0.0;
        int posiNums = 0;
        int posiSum = 0;
        int firstPosi = -1;
        for (int i=0;i<phaseLength;i++){
            double singlePhaseSum = 0.0;
            for (int j=0;j<cycleNums;j++){
                if (data[i][j] > 0){
                    posiNums++;
                    singlePhaseSum++;
                    posiSum += data[i][j];
                    if (firstPosi < 0){
                        firstPosi = i;
                    }
                }
            }
            dischargePhaseAvg += Math.pow(singlePhaseSum, 2);
        }

        int negNums = 0;
        int negSum = 0;
        int firstNeg = -1;
        for (int i=phaseLength/2;i<phaseLength;i++){
            double singlePhaseSum = 0.0;
            for (int j=0;j<cycleNums;j++){
                if (data[i][j] > 0){
                    negNums++;
                    singlePhaseSum++;
                    negSum += data[i][j];
                    if (firstNeg < 0){
                        firstNeg = i;
                    }
                }
            }
            dischargePhaseAvg += Math.pow(singlePhaseSum, 2);
        }
        dischargePhaseAvg /= posiNums + negNums + EPSILON;

        double dischargePhaseSigma = 0.0;
        double dischargePhaseSkew = 0.0;
        for (int i=0;i<phaseLength;i++){
            double singlePhaseSum = 0.0;
            for (int j=0;j<cycleNums;j++)
                singlePhaseSum++;
            dischargePhaseSigma += Math.pow(singlePhaseSum - dischargePhaseAvg, 2);
            dischargePhaseSkew += Math.pow(singlePhaseSum - dischargePhaseAvg, 3);
        }
        dischargePhaseSigma /= posiNums + negNums + EPSILON;
        dischargePhaseSkew /= Math.pow(dischargePhaseSigma, 3);

        int posi = 0;
        int neg = 0;
        int posiNum1 = 0;
        int posiNum2 = 0;
        int negNum1 = 0;
        int negNum2 = 0;
        int halfPhase = cycleNums / 2 + 1;
        int quarterPhase = cycleNums / 4 + 1;
        for (int i=0;i<phaseLength;i++){
            for (int j=0;j<cycleNums;j++){
                int dischargeTmp = data[i][j];
                if (j < halfPhase){
                    posi++;
                    if (j < quarterPhase && dischargeTmp > 0)
                        posiNum1++;
                    if (j >= quarterPhase && dischargeTmp > 0)
                        posiNum2++;
                }
                else {
                    neg++;
                    if (j < quarterPhase && dischargeTmp > 0)
                        negNum1++;
                    if (j >= quarterPhase && dischargeTmp > 0)
                        negNum2++;
                }
            }
        }
        double internalFeature1 = posiNum1 / (negNum1 + EPSILON);
        double internalFeature2 = (posiNum1 + posiNum2) / (negNum1 + negNum2 + EPSILON);
        double internalFeature3 = posiNum2 / (posiNum1 + EPSILON);
        double internalFeature4 = negNum2 / (negNum1 + EPSILON);

        double surfaceFeature1 = negNums / (posiNums + EPSILON);

        double coronaFeature1 = posiNums / (negNums + EPSILON);

        featureVector[idx++] = (negSum * posiNums) / (negNums * posiSum + EPSILON);//65
        featureVector[idx++] =  firstPosi - firstNeg;//66
        featureVector[idx++] = posiPhaseWidth;//67
        featureVector[idx++] = negPhaseWidth;//68
        featureVector[idx++] = dischargePhaseAvg;//69
        featureVector[idx++] = dischargePhaseSigma;//70
        featureVector[idx++] = dischargePhaseSkew;//71
        featureVector[idx++] = internalFeature1;//72
        featureVector[idx++] = internalFeature2;//73
        featureVector[idx++] = internalFeature3;//74
        featureVector[idx++] = internalFeature4;//75
        featureVector[idx++] = surfaceFeature1;//76
        featureVector[idx++] = coronaFeature1;//77
        featureVector[idx++] = cycleNums;//78
        if (idx > UHFFeatureCnt)
            System.out.println("UHF特征提取错误");
        return featureVector;
    }

    public void getLabelEncoder(){
        if (label == 2)
            label = 1;
        if (label == 3)
            label = 2;
        if (label == 5)
            label = 3;
        if (label == 7)
            label = 4;
    }

}

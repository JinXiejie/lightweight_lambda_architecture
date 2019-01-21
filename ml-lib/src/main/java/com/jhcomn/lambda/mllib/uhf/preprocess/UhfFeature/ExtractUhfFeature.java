package com.jhcomn.lambda.mllib.uhf.preprocess.UhfFeature;

public class ExtractUhfFeature {
    private static double EPSILON = 0.0001;
    private int[][] prpsData = null;
    private int label = -1;
    public ExtractUhfFeature(int[][] prpsData, int label){
        this.prpsData = prpsData;
        this.label = label;
    }

    public String extractFeature(){
        StringBuffer trainData = new StringBuffer();
        String labelTmp = String.valueOf(label) + ";";
        trainData.append(labelTmp);
        int phaseLength = prpsData.length;
        int cycleNums = prpsData[0].length;
        int posiPhaseWidth = 0;
        int negPhaseWidth = 0;

        for (int i=0;i<phaseLength;i++){
            double phaseDischargeAvg = 0.0;

            for (int j=0;j<cycleNums;j++){
                phaseDischargeAvg += prpsData[i][j];
            }

            phaseDischargeAvg /= (double)cycleNums;
            String phaseDischargeAvgTmp = String.valueOf(phaseDischargeAvg) + ";";
            trainData.append(phaseDischargeAvgTmp);
            if (phaseDischargeAvg > 0){
                if (i < 32)
                    posiPhaseWidth += 1;
                else
                    negPhaseWidth += 1;
            }
        }
        String posiPhaseWidthTmp = String.valueOf(posiPhaseWidth) + ";";
        String negPhaseWidthTmp = String.valueOf(negPhaseWidth) + ";";
        trainData.append(posiPhaseWidthTmp);
        trainData.append(negPhaseWidthTmp);


        double dischargePhaseAvg = 0.0;
        int posiNums = 0;
        int posiSum = 0;
        int firstPosi = -1;
        for (int i=0;i<phaseLength;i++){
            double singlePhaseSum = 0.0;
            for (int j=0;j<cycleNums;j++){
                if (prpsData[i][j] > 0){
                    posiNums++;
                    singlePhaseSum++;
                    posiSum += prpsData[i][j];
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
                if (prpsData[i][j] > 0){
                    negNums++;
                    singlePhaseSum++;
                    negSum += prpsData[i][j];
                    if (firstNeg < 0){
                        firstNeg = i;
                    }
                }
            }
            dischargePhaseAvg += Math.pow(singlePhaseSum, 2);
        }
        double posiNegDischarge = (negSum * posiNums) / (negNums * posiSum + EPSILON);
        String posiNegDischargeTmp = String.valueOf(posiNegDischarge) + ";";

        double firstPhaseDifference = firstNeg - firstPosi;
        String firstPhaseDifferenceTmp = String.valueOf(firstPhaseDifference) + ";";

        dischargePhaseAvg = dischargePhaseAvg / (posiNums + negNums + EPSILON);
        String dischargePhaseAvgTmp = String.valueOf(dischargePhaseAvg) + ";";

        trainData.append(posiNegDischargeTmp);
        trainData.append(firstPhaseDifferenceTmp);
        trainData.append(dischargePhaseAvgTmp);



        return trainData.toString();
    }

}

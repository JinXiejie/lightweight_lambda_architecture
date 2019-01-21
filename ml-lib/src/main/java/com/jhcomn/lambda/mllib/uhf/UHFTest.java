package com.jhcomn.lambda.mllib.uhf;

import com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader.UHFDataReaderDat;

import java.io.IOException;

public class UHFTest {
    public static void main(String[] args) throws IOException{
        UHFDataReaderDat uhfDataReaderDat = new UHFDataReaderDat();
        String filePath = "E:\\PRPSData\\PDMSystemPdmSys_CouplerSPDC0001";
        String destFilePath = "E:\\PRPSData\\PRPS\\prps_train";
        uhfDataReaderDat.prpsDataParser(filePath);
    }
}

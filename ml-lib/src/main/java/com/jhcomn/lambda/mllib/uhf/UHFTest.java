package com.jhcomn.lambda.mllib.uhf;

import com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader.UHFDataReaderDat;

import java.io.IOException;

public class UHFTest {
    public static void main(String[] args) throws IOException{
        UHFDataReaderDat uhfDataReaderDat = new UHFDataReaderDat();
//        String filePath = "E:\\PRPSData\\PDMSystemPdmSys_CouplerSPDC0001";

        String urlStr = "http://www.youdiancloud.com/PDMSystem-PdmSys-CouplerSPDC0001_01_20140611235118.dat";
        String fileName = "uhfDat.dat";
        String savePath = "E:\\JinXiejie\\UHFuRLData";
        uhfDataReaderDat.uhfAnalyzeReader(urlStr, fileName, savePath);

    }
}
//http://www.youdiancloud.com/PDMSystem-PdmSys-CouplerSPDC0001_01_20140611235118.dat
//http://www.youdiancloud.com/PDMSystem-PdmSys-CouplerSPDC0001_01_20140611235502.dat
//http://www.youdiancloud.com/PDMSystem-PdmSys-CouplerSPDC0001_01_20140612000449.dat

package com.jhcomn.lambda.mllib.uw1000;

import com.jhcomn.lambda.mllib.uw1000.preprocess.UW1000LocalPreprocessUtil;
import com.jhcomn.lambda.mllib.uw1000.training.svm.SVMController;

/**
 * Created by shimn on 2017/5/22.
 */
public class Test {

    public static void main(String[] args) {
        //小波降噪
//        Wavelet wavelet = null;
//        if (wavelet == null)
//            wavelet = DeNoiser.createWaveletDenoiser();
//        long begin = System.currentTimeMillis();
//        float vals[] = { 32.0f, 10.0f, 20.0f, 38.0f,
//                37.0f, 28.0f, 38.0f, 34.0f,
//                18.0f, 24.0f, 18.0f, 9.0f,
//                23.0f, 24.0f, 28.0f, 34.0f,
//                23.0f, 24.0f, 28.0f, 34.0f};
//        float[] vals1 = {3f, 1f, 0f, 4f, 8f, 6f, 9f};
//        float[] result = wavelet.waveletDenoise(vals, 3, WaveEnum.Haar);
//        long end = System.currentTimeMillis();
//        System.out.println("wavelet use time = " + (end - begin));
//
//        for (int i = 0; i < result.length; i++) {
//            System.out.print(result[i] + ", ");
//        }

        UW1000LocalPreprocessUtil uw1000 = new UW1000LocalPreprocessUtil();

        //论文特征提取章节
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\论文数据\\特征提取22维\\discharge10",
//                "D:\\jhcomnBigData\\uw_classify\\source\\论文数据\\特征提取22维\\discharge10.data",
//                "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\论文数据\\特征提取22维\\normal10",
//                "D:\\jhcomnBigData\\uw_classify\\source\\论文数据\\特征提取22维\\normal10.data",
//                "0");
//
//        //svm
//        uw1000.generateFeaturesIrisWithPCA("D:\\jhcomnBigData\\uw_classify\\source\\backup\\discharge_new",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "discharge_td7_fd3.data",
//                "1");
//        uw1000.generateFeaturesIrisWithPCA("D:\\jhcomnBigData\\uw_classify\\source\\backup\\normal_new",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "normal_td7_fd3.data",
//                "0");

        //2017-12-13论文数据测试pca
//        uw1000.generateAllFeaturesIrisWithPCA("D:\\jhcomnBigData\\uw_classify\\source\\backup\\discharge_santai",
//                "D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withpca_all\\21\\" + "all.data",
//                "1",
//                "D:\\jhcomnBigData\\uw_classify\\source\\backup\\normal_santai",
//                "D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withpca_all\\21\\" + "normal.data",
//                "0");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\backup\\discharge_santai",
//            "D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withoutpca\\xgb22\\" + "discharge.data",
//            "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\backup\\normal_santai",
//            "D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withoutpca\\xgb22\\" + "normal.data",
//            "0");

//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\未上传_局放信号",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\" + "discharge1.data",
//                "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\31uwaudiodata",
//                "C:\\Users\\shimn\\Desktop\\" + "31.data",
//                "0");

//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\超声波未标签音频20171101-已分类\\干扰",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\超声波未标签音频20171101-已分类\\干扰\\" + "normal.data",
//                "0");

        //withoutpca svm
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\backup\\discharge_santai",
//            "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\santai\\mfcc12_td7_fd3\\discharge.data",
//            "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\source\\backup\\normal_santai",
//            "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\santai\\mfcc12_td7_fd3\\normal.data",
//            "0");
//
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\局放汇总",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\discharge_svm.data",
//                "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\未上传_局放信号",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\discharge1_svm.data",
//                "1");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\31uwaudiodata",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\31_svm.data",
//                "0");
//        uw1000.generateFeaturesIris("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\测试数据_背景及干扰",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\bac_normal_svm.data",
//                "0");

        //withoutpca xgb
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\traindata\\source\\discharge\\",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\traindata\\source\\discharge.data",
//                "1");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\traindata\\source\\normal\\",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\traindata\\source\\normal.data",
//                "0");

        //真实环境数据
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\discharge\\",
//                "D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\discharge.data",
//                "1");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\normal\\",
//                "D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\normal.data",
//                "0");

        //uw1000.generateFeaturesXGB("C:\\Users\\shimn\\Desktop\\wrongs\\",
        //        "C:\\Users\\shimn\\Desktop\\wrongs\\normal.data",
         //       "0");

//        uw1000.generateFeaturesXGB("F:\\tmpuw\\discharge\\",
//                "F:\\tmpuw\\discharge.data",
//                "1");

        uw1000.generateFeaturesXGB("F:\\tmpuw\\normal\\",
                "F:\\tmpuw\\normal.data",
                "0");


//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\normal\\",
//                "D:\\jhcomnBigData\\uw_classify\\uw_wav_file\\traindata\\source\\normal.data",
//                "0");

        //模拟数据
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\20180314\\discharge\\",
//                "D:\\jhcomnBigData\\uw_classify\\20180314\\discharge.data",
//                "1");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\20180314\\normal\\",
//                "D:\\jhcomnBigData\\uw_classify\\20180314\\normal.data",
//                     "0");

//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\局放汇总",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\discharge_512.data",
//                "1");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\未上传_局放信号",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\discharge1_512.data",
//                "1");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\31uwaudiodata",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\31_512.data",
//                "0");
//        uw1000.generateFeaturesXGB("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\测试数据_背景及干扰",
//                "D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\测试数据\\无局放汇总\\bac_normal_512.data",
//                "0");

        //test
//        uw1000.generateFeaturesIrisWithPCA("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\20171113\\局放",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "test_d.data",
//                "1");
//        uw1000.generateFeaturesIrisWithPCA("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\20171113\\无局放",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "test_n.data",
//                "0");

        //SVMController svmController = new SVMController();

        //santai
//        svmController.trainLocal("C:\\Users\\shimn\\Desktop\\train_svm.data",
//                "C:\\Users\\shimn\\Desktop\\");
//        EvaluationMetrics metrics = svmController.predictAllLocal("C:\\Users\\shimn\\Desktop\\test_svm\\test_64_17.data",
//                "C:\\Users\\shimn\\Desktop\\");
//        svmController.writeResultsFile("C:\\Users\\shimn\\Desktop\\",
//                "C:\\Users\\shimn\\Desktop\\",
//                metrics);

        //new
//        svmController.trainLocal("D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\all.data",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10");
//        EvaluationMetrics metrics = svmController.predictAllLocal("D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "test_new.data",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\");
//        svmController.writeResultsFile("D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\",
//                metrics);


        //2017-12-13论文数据测试
//        svmController.trainLocal("D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withpca\\10\\all.data",
//                "D:\\jhcomnBigData\\uw_classify\\论文数据依据\\withpca\\10\\");
//        EvaluationMetrics metrics = svmController.predictAllLocal("D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\" + "test_new.data",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\");
//        svmController.writeResultsFile("D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10",
//                "D:\\jhcomnBigData\\uw_classify\\source\\svm\\train\\new\\10\\",
//                metrics);

//        XGBPreProcess preProcess = new XGBPreProcess();
//        XGBTraining training = new XGBTraining();
//        try {
//            int[] nums = preProcess.prepProcessLocalBeforeTraining(
//                    XGBProperties.PARENT_DIR + File.separator + "discharge_all",
//                    XGBProperties.PARENT_DIR + File.separator + "normal_all",
//                    null, null);
//            training.trainLocal(preProcess.dischargeLocalFile, nums[0],
//                    preProcess.normalLocalFile, nums[1],
//                    XGBProperties.MODEL_DIR);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String[] models = new File(XGBProperties.MODEL_DIR).list();
//        XGBPredict xgbPredict = new XGBPredict();
//        for (String model : models) {
//            xgbPredict.predictLocal(XGBProperties.TEST_DATA_DIR + File.separator + "test.data",
//                    model);
//        }
    }

//    public static int toInt(byte[] b) {
//        return ((b[3] << 24) + (b[2] << 16) + (b[1] << 8) + (b[0] << 0));
//    }
//
//    public static short toShort(byte[] b) {
//        return (short)((b[1] << 8) + (b[0] << 0));
//    }
//
//
//    public static byte[] read(RandomAccessFile rdf, int pos, int length) throws IOException {
//        rdf.seek(pos);
//        byte result[] = new byte[length];
//        for (int i = 0; i < length; i++) {
//            result[i] = rdf.readByte();
//        }
//        return result;
//    }
//
//
//    public static void main(String[] args) throws IOException {
//
//        File f = new File("D:\\jhcomnBigData\\uw_classify\\超声波音频文件\\声音\\声音\\test\\REC060.wav");
//        RandomAccessFile rdf = null;
//        rdf = new RandomAccessFile(f,"r");
//
//        System.out.println("audio size: " + toInt(read(rdf, 4, 4))); // 声音尺寸
//
//        System.out.println("audio format: " + toShort(read(rdf, 20, 2))); // 音频格式 1 = PCM
//
//        System.out.println("num channels: " + toShort(read(rdf, 22, 2))); // 1 单声道 2 双声道
//
//        System.out.println("sample rate: " + toInt(read(rdf, 24, 4)));  // 采样率、音频采样级别 8000 = 8KHz
//
//        System.out.println("byte rate: " + toInt(read(rdf, 28, 4)));  // 每秒波形的数据量
//
//        System.out.println("block align: " + toShort(read(rdf, 32, 2)));  // 采样帧的大小
//
//        System.out.println("bits per sample: " + toShort(read(rdf, 34, 2)));  // 采样位数
//
//        rdf.close();
//
//    }

}

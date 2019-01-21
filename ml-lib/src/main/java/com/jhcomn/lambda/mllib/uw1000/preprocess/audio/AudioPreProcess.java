package com.jhcomn.lambda.mllib.uw1000.preprocess.audio;

import com.jhcomn.lambda.mllib.uw1000.preprocess.audio.denoise.WaveEnum;
import com.jhcomn.lambda.mllib.uw1000.preprocess.audio.denoise.Wavelet;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.FDFeature;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.IFeatureCallback;
import com.jhcomn.lambda.mllib.uw1000.preprocess.feature.TDFeature;

/**
 * 预处理过程
 *
 * @author wanggang
 *
 */
public class AudioPreProcess {

	float[] originalSignal;// initial extracted PCM,
	float[] afterEndPtDetection;// after endPointDetection
	public int noOfFrames;// calculated total no of frames
	int samplePerFrame;// how many samples in one frame
	int framedArrayLength;// how many samples in framed array
	public float[][] framedSignal;
	float[] hammingWindow;
	EndPointDetection epd;
	int samplingRate;

    //新增特征回调实例
    private IFeatureCallback fCallback = null;
    //时域特征
    private TDFeature tdFeature = null;
	//小波降噪
    private Wavelet wavelet = null;

	/**
	 * constructor, all steps are called frm here
	 *
	 * @param originalSignal
	 *            extracted PCM data
	 * @param samplePerFrame
	 *            how many samples in one frame,=660 << frameDuration, typically
	 *            30; samplingFreq, typically 22Khz
	 */
	public AudioPreProcess(float[] originalSignal, int samplePerFrame, int samplingRate) {
		this.originalSignal = originalSignal;
		this.samplePerFrame = samplePerFrame;
		this.samplingRate = samplingRate;

		normalizePCM();
		epd = new EndPointDetection(this.originalSignal, this.samplingRate);
		afterEndPtDetection = epd.doEndPointDetection();
		// ArrayWriter.printFloatArrayToFile(afterEndPtDetection, "endPt.txt");

        //帧内采样
		doFraming();
		//加窗处理
		doWindowing();
	}

    public AudioPreProcess(float[] originalSignal, int samplePerFrame, int samplingRate, IFeatureCallback fCallback) {
        this.originalSignal = originalSignal;
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;
        this.fCallback = fCallback;

        //归一化pcm数据
        normalizePCM();
        //端点检测
        epd = new EndPointDetection(this.originalSignal, this.samplingRate);
        afterEndPtDetection = epd.doEndPointDetection();

        //小波降噪
//        if (wavelet == null)
//            wavelet = DeNoiser.createWaveletDenoiser();
        //帧内采样
        doFraming();
        //加窗处理
        doWindowing();

        //时域特征
        TDFeature td = TDFeature.newInstance();
        td.HZCRR(td.generateTDFeaturesRetZCR(framedSignal, 0.02f));
        //新增特征回调数据
        this.fCallback.onTDFeatureCallback(td);

		//频域特征
		FDFeature fd = FDFeature.newInstance();
        fd.generateExtentFeatures(fd.generatePSD(framedSignal, samplePerFrame));
		this.fCallback.onFDFeatureCallback(fd);

    }

    private void normalizePCM() {
		float max = originalSignal[0];
		for (int i = 1; i < originalSignal.length; i++) {
			if (max < Math.abs(originalSignal[i])) {
				max = Math.abs(originalSignal[i]);
			}
		}
		// System.out.println("max PCM =  " + max);
		for (int i = 0; i < originalSignal.length; i++) {
			originalSignal[i] = originalSignal[i] / max;
		}
	}

    /**
     * 信号分帧，并对每帧做小波降噪
     */
    private void doFramingDenoise() {
        // calculate no of frames, for framing

        noOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
        //		System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame
        //				+ "  EPD length   " + afterEndPtDetection.length);
        framedSignal = new float[noOfFrames][samplePerFrame];
        for (int i = 0; i < noOfFrames; i++) {
            int startIndex = (i * samplePerFrame / 2);
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
            framedSignal[i] = wavelet.waveletDenoise(framedSignal[i], 3, WaveEnum.Haar);
        }
    }

	/**
	 * divides the whole signal into frames of samplerPerFrame
	 */
	private void doFraming() {
		// calculate no of frames, for framing

		noOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
		//		System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame
		//				+ "  EPD length   " + afterEndPtDetection.length);
		if (noOfFrames <= 0) {
//			System.out.println("端点检测后长度 = " + afterEndPtDetection.length + ", samplePerFrame = " + samplePerFrame);
			framedSignal = new float[1][samplePerFrame];
			for (int i = 0; i < afterEndPtDetection.length; i++) {
				framedSignal[0][i] = afterEndPtDetection[i];
			}
			for (int i = afterEndPtDetection.length; i < samplePerFrame; i++) {
				framedSignal[0][i] = 0f;
			}
		}
		else {
			framedSignal = new float[noOfFrames][samplePerFrame];	//TODO java.lang.NegativeArraySizeException
			for (int i = 0; i < noOfFrames; i++) {
				int startIndex = (i * samplePerFrame / 2);
				for (int j = 0; j < samplePerFrame; j++) {
					framedSignal[i][j] = afterEndPtDetection[startIndex + j];
				}
			}
		}
	}

	/**
	 * does hamming window on each frame
	 */
	private void doWindowing() {
		// prepare hammingWindow
		hammingWindow = new float[samplePerFrame + 1];
		// prepare for through out the data
		for (int i = 1; i <= samplePerFrame; i++) {

			hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
		}
		// do windowing
		for (int i = 0; i < noOfFrames; i++) {
			for (int j = 0; j < samplePerFrame; j++) {
				framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
			}
		}
	}

    /**
     * 加窗并去噪
     */
    private void doWindowingDenoise() {
        // prepare hammingWindow
        hammingWindow = new float[samplePerFrame + 1];
        // prepare for through out the data
        for (int i = 1; i <= samplePerFrame; i++) {
            hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }
        // do windowing
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
            }
//            long begin = System.currentTimeMillis();
            framedSignal[i] = wavelet.waveletDenoise(framedSignal[i], 3, WaveEnum.Haar);
//            long end = System.currentTimeMillis();
//            System.out.println("wavelet every frame " + i + " use time = " + (end - begin));
        }
    }
}
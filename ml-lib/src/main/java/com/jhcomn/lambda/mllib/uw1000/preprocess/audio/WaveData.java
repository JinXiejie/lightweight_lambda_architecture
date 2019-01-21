package com.jhcomn.lambda.mllib.uw1000.preprocess.audio;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import javax.sound.sampled.*;

/**
 * 从音频文件字节数组中提取PCM数据并保存
 *
 * @author wanggang
 *
 */
public class WaveData {

	private byte[] arrFile;
	private byte[] audioBytes;
	private float[] audioData;
	private FileOutputStream fos;
	private ByteArrayInputStream bis;
	private AudioInputStream audioInputStream;
	private AudioFormat format;
	private double durationSec;

    //开辟10M的缓存空间
//    private final ByteBuffer cache = ByteBuffer.allocate(10 * 1024 * 1024);

	public WaveData() {
		//
	}

	public byte[] getAudioBytes() {
		return audioBytes;
	}

	public double getDurationSec() {
		return durationSec;
	}

	public float[] getAudioData() {
		return audioData;
	}

	public AudioFormat getFormat() {
		return format;
	}

	private void bugPrint (byte[] data) {
		int len = data.length;
		StringBuffer sb = new StringBuffer("[");
		for(int i = 0; i < len; i++)
			sb.append(data[i] + ", ");
		sb.append("]");
		File file = new File("./test.log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("data = " + sb.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * local模式
	 * @param waveFile
	 * @return
     */
	public float[] extractAmplitudeFromFile(File waveFile) {
		// 创建文件输入流
		try (FileInputStream fis = new FileInputStream(waveFile)) {
			// 从文件中创建字节数组
			arrFile = new byte[(int) waveFile.length()];
			fis.read(arrFile);
		} catch (Exception e) {
			System.err.println("SomeException : " + e.toString());
		}
		float[] result = extractAmplitudeFromFileByteArray(arrFile);
        arrFile = null;
        return result;
	}

	/**
	 * HDFS模式
	 * @param fileSystem
	 * @param path
     * @return
     */
	public float[] extractAmplitudeFromHDFS(FileSystem fileSystem, Path path) {
		if (fileSystem == null || path == null)
			return null;
		try {
			FSDataInputStream fsis = fileSystem.open(path);
			arrFile = new byte[fsis.available()];
			IOUtils.readFully(fsis, arrFile, 0, arrFile.length);	//可用
//			fsis.read(arrFile, 0, arrFile.length);	//存在数据读取丢包问题，不可用
		} catch (IOException e) {
			System.out.println("extractAmplitudeFromHDFS exception : " + e.toString());
			return null;
		}
		float[] result = extractAmplitudeFromFileByteArray(arrFile);
		arrFile = null;
		return result;
	}

	public float[] extractAmplitudeFromFileByteArray(byte[] arrFile) {
		//		System.out.println("File :  " + wavFile + "" + arrFile.length);
		bis = new ByteArrayInputStream(arrFile);
		return extractAmplitudeFromFileByteArrayInputStream(bis);
	}

	/**
	 * for extracting amplitude array the format we are using :16bit, 22khz, 1
	 * channel, littleEndian,
	 *
	 * @return PCM音频数据
	 * @throws Exception
	 */
	public float[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(bis);
            float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat()
                    .getFrameRate());
            durationSec = milliseconds / 1000.0;
            return extractFloatDataFromAudioInputStream(audioInputStream);

		} catch (UnsupportedAudioFileException e) {
			System.out.println("unsupported file type, during extract amplitude");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException during extracting amplitude");
			e.printStackTrace();
		}
		return null;
	}

	public float[] extractFloatDataFromAudioInputStream(AudioInputStream audioInputStream) {
		format = audioInputStream.getFormat();
		//TODO
		audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
		// calculate durationSec
		float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
		durationSec = milliseconds / 1000.0;
		// System.out.println("The current signal has duration "+durationSec+" Sec");
		try {
			audioInputStream.read(audioBytes);
		} catch (IOException e) {
			System.out.println("IOException during reading audioBytes");
			e.printStackTrace();
		}

		return extractFloatDataFromAmplitudeByteArray(format, audioBytes);
	}

	public float[] extractFloatDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {
		// convert
		audioData = null;
		if (format.getSampleSizeInBits() == 16) {
			int nlengthInSamples = audioBytes.length / 2;
			audioData = new float[nlengthInSamples];
			if (format.isBigEndian()) {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is MSB (high order) */
					int MSB = audioBytes[2 * i];
					/* Second byte is LSB (low order) */
					int LSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			} else {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is LSB (low order) */
					int LSB = audioBytes[2 * i];
					/* Second byte is MSB (high order) */
					int MSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		} else if (format.getSampleSizeInBits() == 8) {
			int nlengthInSamples = audioBytes.length;
			audioData = new float[nlengthInSamples];
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i];
				}
			} else {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i] - 128;
				}
			}
		}// end of if..else
			// System.out.println("PCM Returned===============" +
			// audioData.length);
		return audioData;
	}

	/**
	 * Save to file.
	 *
	 * @param name
	 *            the name
	 * @param fileType
	 *            the file type
	 */
	public void saveToFile(String name, AudioFileFormat.Type fileType, AudioInputStream audioInputStream) {
		File myFile = new File(name);
		if (!myFile.exists())
			myFile.mkdir();

		if (audioInputStream == null) {
			return;
		}
		// reset to the beginnning of the captured data
		try {
			audioInputStream.reset();
		} catch (Exception e) {
			return;
		}
		myFile = new File(name + ".wav");
		int i = 0;
		while (myFile.exists()) {
			String temp = String.format(name + "%d", i++);
			myFile = new File(temp + ".wav");
		}
		try {
			if (AudioSystem.write(audioInputStream, fileType, myFile) == -1) {
			}
		} catch (Exception ex) {
		}
		System.out.println(myFile.getAbsolutePath());
		// JOptionPane.showMessageDialog(null, "File Saved !", "Success",
		// JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * saving the file's bytearray
	 *
	 * @param fileName
	 *            the name of file to save the received byteArray of File
	 */
	public void saveFileByteArray(String fileName, byte[] arrFile) {
		try {
			fos = new FileOutputStream(fileName);
			fos.write(arrFile);
			fos.close();
		} catch (Exception ex) {
			System.err.println("Error during saving wave file " + fileName + " to disk" + ex.toString());
		}
		System.out.println("WAV Audio data saved to " + fileName);
	}
}
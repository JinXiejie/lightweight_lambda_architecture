package com.jhcomn.lambda.mllib.uhf.preprocess.dataTransmit;

import java.io.Serializable;

public class AnalyseResult implements Serializable {
	private String content;

	public AnalyseResult() {
	}

	public AnalyseResult(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

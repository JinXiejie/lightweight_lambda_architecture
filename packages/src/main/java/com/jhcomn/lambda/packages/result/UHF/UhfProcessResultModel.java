package com.jhcomn.lambda.packages.result.UHF;

import com.jhcomn.lambda.packages.result.ProcessResultModel;
import com.jhcomn.lambda.packages.tag.uhf.UhfTagWithResult;

import java.util.List;

public class UhfProcessResultModel extends ProcessResultModel {
    private List<UhfTagWithResult> tag;

    public UhfProcessResultModel() {
    }

    public UhfProcessResultModel(String file, List<UhfTagWithResult> tag) {
        super(file);
        this.tag = tag;
    }

    public List<UhfTagWithResult> getTag() {
        return tag;
    }

    public void setTag(List<UhfTagWithResult> tag) {
        this.tag = tag;
    }
}

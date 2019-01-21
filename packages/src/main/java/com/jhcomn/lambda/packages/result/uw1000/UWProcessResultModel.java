package com.jhcomn.lambda.packages.result.uw1000;

import com.jhcomn.lambda.packages.result.ProcessResultModel;
import com.jhcomn.lambda.packages.tag.Tag;
import com.jhcomn.lambda.packages.tag.uw1000.UWTagWithResult;

import java.util.List;

/**
 * Created by shimn on 2018/1/22.
 */
public class UWProcessResultModel extends ProcessResultModel {

    private List<UWTagWithResult> tag;

    public UWProcessResultModel() {
    }

    public UWProcessResultModel(String file, List<UWTagWithResult> tag) {
        super(file);
        this.tag = tag;
    }

    public List<UWTagWithResult> getTag() {
        return tag;
    }

    public void setTag(List<UWTagWithResult> tag) {
        this.tag = tag;
    }
}

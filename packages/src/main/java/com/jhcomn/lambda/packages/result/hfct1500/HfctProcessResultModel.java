package com.jhcomn.lambda.packages.result.hfct1500;

import com.jhcomn.lambda.packages.result.ProcessResultModel;
import com.jhcomn.lambda.packages.tag.hfct1500.HfctTagWithResult;

import java.util.List;

/**
 * Created by shimn on 2017/5/25.
 */
public class HfctProcessResultModel extends ProcessResultModel {
    private List<HfctTagWithResult> tag;

    public HfctProcessResultModel() {
    }

    public HfctProcessResultModel(String file, List<HfctTagWithResult> tag) {
        super(file);
        this.tag = tag;
    }

    public List<HfctTagWithResult> getTag() {
        return tag;
    }

    public void setTag(List<HfctTagWithResult> tag) {
        this.tag = tag;
    }
}

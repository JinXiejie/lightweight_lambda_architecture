package com.jhcomn.lambda.packages.tag.hfct1500;

import com.jhcomn.lambda.packages.tag.Tag;

/**
 * HFCT分析结果
 * Created by shimn on 2017/5/25.
 */
public class HfctTagWithResult extends Tag {

    private String rate;

    public HfctTagWithResult() {
    }

    public HfctTagWithResult(Tag tag, String rate) {
        setTag(tag.getTag());
        setName(tag.getName());
        setTagId(tag.getTagId());
        setType(tag.getType());
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}

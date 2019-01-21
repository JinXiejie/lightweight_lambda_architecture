package com.jhcomn.lambda.packages.tag.uw1000;

import com.jhcomn.lambda.packages.tag.Tag;

/**
 * Created by shimn on 2018/1/23.
 */
public class UWTagWithResult extends Tag {

    private float rate;

    public UWTagWithResult() {
    }

    public UWTagWithResult(Tag tag, float rate) {
        setTag(tag.getTag());
        setName(tag.getName());
        setTagId(tag.getTagId());
        setType(tag.getType());
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "type='" + getType() + '\'' +
                ", tagId='" + getTagId() + '\'' +
                ", name='" + getName() + '\'' +
                ", tag='" + getTag() + '\'' +
                ", rate='" + rate + '\''  +
                '}';
    }
}

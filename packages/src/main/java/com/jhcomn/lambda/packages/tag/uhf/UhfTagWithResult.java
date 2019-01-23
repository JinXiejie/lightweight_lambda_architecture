package com.jhcomn.lambda.packages.tag.uhf;

import com.jhcomn.lambda.packages.tag.Tag;

public class UhfTagWithResult extends Tag{
    private float rate;

    public UhfTagWithResult() {
    }

    public UhfTagWithResult(Tag tag, float rate) {
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

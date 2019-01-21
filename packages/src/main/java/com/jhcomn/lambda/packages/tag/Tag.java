package com.jhcomn.lambda.packages.tag;

import com.jhcomn.lambda.packages.IPackage;


/**
 * 标签
 * Created by shimn on 2017/5/22.
 */
public class Tag implements IPackage {

    private static final long serialVersionUID = 1L;

    //标签所属类型，与后台同步
    private String type;

    //标签id，与后台同步
    private String tagId;

    //标签名，与后台同步
    private String name;

    //标签转换值，与算法同步
    private String tag;

    public Tag() {
    }

    public Tag(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "type='" + type + '\'' +
                ", tagId='" + tagId + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}

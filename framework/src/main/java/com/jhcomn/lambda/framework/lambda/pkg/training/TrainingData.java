package com.jhcomn.lambda.framework.lambda.pkg.training;

import com.jhcomn.lambda.packages.IPackage;

/**
 * 原始数据model
 * Created by shimn on 2016/12/20.
 */
public class TrainingData implements IPackage {

    //数据文件名称
    private String file;

    //数据标签
    private String tag;

    //标签id
    private String tag_id;

    //图谱类型id
    private String typeId;

    //图谱类型，如：prpd
    private String typeName;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TrainingData{" +
                "file='" + file + '\'' +
                ", tag='" + tag + '\'' +
                ", tag_id='" + tag_id + '\'' +
                ", typeId='" + typeId + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}

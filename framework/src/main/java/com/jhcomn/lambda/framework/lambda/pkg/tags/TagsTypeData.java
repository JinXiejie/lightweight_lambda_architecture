package com.jhcomn.lambda.framework.lambda.pkg.tags;

import com.jhcomn.lambda.packages.IPackage;

import java.util.List;

/**
 * {
 *     "type":"HFCT",
 *     "taglist":[TagsData]
 * }
 * Created by shimn on 2017/5/18.
 */
public class TagsTypeData implements IPackage{

    private String type;

    private List<TagsData> taglist;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TagsData> getTaglist() {
        return taglist;
    }

    public void setTaglist(List<TagsData> taglist) {
        this.taglist = taglist;
    }

    @Override
    public String toString() {
        return "TagsTypeData{" +
                "type='" + type + '\'' +
                ", taglist=" + taglist +
                '}';
    }
}

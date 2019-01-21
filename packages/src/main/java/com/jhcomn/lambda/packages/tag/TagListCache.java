package com.jhcomn.lambda.packages.tag;

import com.jhcomn.lambda.packages.IPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 某个type标签列表缓存类
 * Created by shimn on 2017/5/22.
 */
public class TagListCache implements IPackage {

    private static final long serialVersionUID = 1L;

    private List<Tag> tagList;

    public TagListCache() {
        tagList = new ArrayList<>();
    }

    public TagListCache(List<Tag> tags) {
        if (this.tagList == null) {
            this.tagList = new ArrayList<>();
        }
        for (Tag tag : tags)
            this.tagList.add(tag);
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tags) {
        if (this.tagList == null) {
            this.tagList = new ArrayList<>();
        }
        for (Tag tag : tags)
            this.tagList.add(tag);
    }
}

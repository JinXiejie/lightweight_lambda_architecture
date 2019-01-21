package com.jhcomn.lambda.mllib.base.adapter;

import com.jhcomn.lambda.packages.common.ApplicationDatas;
import com.jhcomn.lambda.packages.tag.Tag;
import com.jhcomn.lambda.packages.tag.TagListCache;

import java.util.List;

/**
 * Created by shimn on 2017/5/22.
 */
public class BaseTagAdapter implements ITagAdapter {

    protected Tag[] tags = null;

    public void update(String type) {
        TagListCache listCache = ApplicationDatas.tagListCacheMap.get(type);
        if (listCache == null || listCache.getTagList() == null || listCache.getTagList().size() == 0) {
            System.out.println("TagAdapter error : " + type + " taglist is null");
        }
        else {
            List<Tag> tagList = listCache.getTagList();
            tags = tagList.toArray(new Tag[tagList.size()]);
        }
    }

    public void update(Tag[] tags) {
        if (tags != null) {
            int len = tags.length;
            this.tags = new Tag[len];
            for (int i = 0; i < len; i++)
                this.tags[i] = tags[i];
        }
    }

    /**
     * HFCT结果数组的下标对应tags数组的下标，根据index拿到Tag详细信息
     * @param index
     * @return
     */
    public Tag getTagByIndex(int index) {
        return tags != null ? tags[index] : null;
    }

    /**
     * 训练时，根据tagId找Tag
     * @param tagId
     * @return
     */
    public Tag getTagById(String tagId) {
        if (tags == null || tagId == null || tagId.equals(""))
            return null;
        for (int i = 0; i < tags.length; i++) {
            Tag tag = tags[i];
            if (tag.getTagId().equalsIgnoreCase(tagId.trim()))
                return tag;
        }
        return null;
    }

    /**
     * 训练时，根据tag信息找Tag完整信息
     * @param tag
     * @return
     */
    public Tag getTagByTag(String tag) {
        if (tags == null || tag == null || tag.equals(""))
            return null;
        for (int i = 0; i < tags.length; i++) {
            Tag t = tags[i];
            if (t.getTag().equalsIgnoreCase(tag.trim()))
                return t;
        }
        return null;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }
}

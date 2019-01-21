package com.jhcomn.lambda.framework.lambda.sync.tag.dispatch.impl;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.packages.common.ApplicationDatas;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsData;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsTypeData;
import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.framework.lambda.sync.tag.TagSyncController;
import com.jhcomn.lambda.mllib.base.adapter.HfctTagAdapter;
import com.jhcomn.lambda.framework.lambda.sync.tag.dispatch.ITagSyncDispatcher;
import com.jhcomn.lambda.packages.tag.Tag;
import com.jhcomn.lambda.packages.tag.TagListCache;
import com.jhcomn.lambda.packages.IPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 传入TagsTypeData数据包
 * Created by shimn on 2017/5/22.
 */
public class TagSyncDispatcher implements ITagSyncDispatcher {

    protected ISyncController syncController = null;

    public TagSyncDispatcher() {
        syncController = new TagSyncController();
    }

    /**
     * 将各type的taglist转换后存入全局HashMap中
     * 根据业务算法不同转换不同
     * @param pkg
     */
    @Override
    public void dispatch(IPackage pkg) {
        Map<String, TagListCache> maps = ApplicationDatas.tagListCacheMap;
        if (maps == null)
            maps = new HashMap<>();

        TagsTypeData raw = (TagsTypeData) pkg;
        TagListCache tagListCache = new TagListCache();
        List<TagsData> tagList = raw.getTaglist();
        if (raw != null) {
            String type = raw.getType();
            if (type.equals(ConstantDatas.UW)) {
                if (tagList != null && tagList.size() > 0) {
                    int size = tagList.size();
                    for (int i = 0; i < size; i++) {
                        Tag tag = new Tag(type);
                        String tagId = tagList.get(i).getId();
                        tag.setTagId(tagId);
                        tag.setName(tagList.get(i).getName());
                        tag.setType(ConstantDatas.UW);
                        //tagId转tag
                        if (tagId.equals("1"))
                            tag.setTag("0");
                        else if (tagId.equals("999"))
                            tag.setTag("1");
                        //add to list
                        tagListCache.getTagList().add(tag);
                    }
                }
            }
            else if (type.equals(ConstantDatas.TEV)) {
            }
            else if (type.equals(ConstantDatas.INFRARED)) {
            }
            else if (type.equals(ConstantDatas.HFCT)) {
                if (tagList != null && tagList.size() > 0) {
                    int size = tagList.size();
                    for (int i = 0; i < size; i++) {
                        Tag tag = new Tag(type);
                        tag.setTagId(tagList.get(i).getId());
                        tag.setName(tagList.get(i).getName());
                        //tagId转tag
                        char[] chr = new char[size];
                        for (int j = 0; j < size; j++) {
                            chr[j] = '0';
                        }
                        chr[i] = '1';
                        tag.setTag(String.valueOf(chr));
                        tag.setType(ConstantDatas.HFCT);
                        //add to list
                        tagListCache.getTagList().add(tag);
                    }
                }
            }
            else if (type.equals(ConstantDatas.TEST)) {
            }
            else if (type.equals(ConstantDatas.HELLO_WORLD)) {
            }
            //更新并持久化标签结果
            maps.put(type, tagListCache);
            //save or update into mysql
            syncController.saveOrUpdate(raw);
        }
        else {
            System.out.println("TagSyncDispatcher error : tag type package is null...");
        }
    }

    public static void main(String[] args) {
        ITagSyncDispatcher dispatcher = new TagSyncDispatcher();
        TagsTypeData typeData = new TagsTypeData();
        typeData.setType(ConstantDatas.HFCT);
        List<TagsData> lists = new ArrayList<>();
        lists.add(new TagsData("1", "内部放电"));
        lists.add(new TagsData("2", "表面放电"));
        lists.add(new TagsData("3", "电晕放电"));
        lists.add(new TagsData("4", "噪声"));
        lists.add(new TagsData("5", "无效数据"));
        typeData.setTaglist(lists);

        dispatcher.dispatch(typeData);

        HfctTagAdapter tagAdapter = new HfctTagAdapter();
        Tag[] tags = tagAdapter.getTags();
        for (Tag tag : tags)
            System.out.println(tag.toString());

        System.out.println(ApplicationDatas.tagListCacheMap);
    }
}

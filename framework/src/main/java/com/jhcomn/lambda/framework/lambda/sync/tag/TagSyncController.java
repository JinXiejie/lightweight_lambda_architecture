package com.jhcomn.lambda.framework.lambda.sync.tag;

import com.alibaba.fastjson.JSONObject;
import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.pkg.training.TrainingRawData;
import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.packages.common.ApplicationDatas;
import com.jhcomn.lambda.framework.lambda.pkg.tags.TagsTypeData;
import com.jhcomn.lambda.framework.lambda.sync.base.AbstractSyncController;
import com.jhcomn.lambda.packages.tag.Tag;
import com.jhcomn.lambda.packages.tag.TagListCache;
import com.jhcomn.lambda.packages.IPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签表tag同步控制器
 * Created by shimn on 2017/5/22.
 */
public class TagSyncController extends AbstractSyncController {

    public static final String TABLENAME = "tag";

    public TagSyncController() {
    }

    @Override
    public void saveOrUpdate(IPackage model) {
        TagsTypeData raw = (TagsTypeData) model;
        String type = raw.getType();
        //step1:先删除相同type的所有记录
        String rmTypeSql = "delete from " + TABLENAME + " where type = ?";
        List<Object> params = new ArrayList<>();
        params.add(type);
        try {
            //删除
            if (jdbcUtils.updateByPreparedStatement(rmTypeSql, params))
                System.out.println("delete type = " + type + " from " + TABLENAME + ", 成功...");
            else
                System.out.println("delete type = " + type + " from " + TABLENAME + ", 失败...");

            //step2:插入转化后的数据
            Map<String, TagListCache> maps = ApplicationDatas.tagListCacheMap;
            if (maps != null) {
                TagListCache tagListCache = maps.get(type);
                if (tagListCache != null) {
                    List<Tag> tagList = tagListCache.getTagList();
                    String insertSql;
                    if (tagList != null) {
                        for (Tag tag : tagList) {
                            //step3:逐条插入
                            insertSql = "insert into " + TABLENAME + " (type, tagId, name, tag) values (?, ?, ?, ?)";
                            params.clear();
                            params.add(tag.getType());
                            params.add(tag.getTagId());
                            params.add(tag.getName());
                            params.add(tag.getTag());
                            //execute
                            if (jdbcUtils.updateByPreparedStatement(insertSql, params)) {
                                System.out.println("insert or update tag 成功...");
                            } else {
                                System.out.println("insert or update tag 失败...");
                            }
                        }
                    } else {System.out.println("tagList is null");}
                } else {System.out.println("tagListCache is null");}
            } else {System.out.println("maps is null");}
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        super.delete(TABLENAME, id);
    }

    @Override
    public IPackage load(String id) {
        return null;
    }

    public List<Tag> loadByType(String type) {
        List<Tag> tags = null;
        String sql = "select type,tagId,name,tag from " + TABLENAME + " where type = ? ";
        List<Object> params = new ArrayList<>();
        params.add(type);
        try {
            tags = jdbcUtils.findMuliRefResult(sql, params, Tag.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

//    static {
//        ISyncController controller = new TagSyncController();
//        //TODO 待改善，更新tag缓存
//        if (ApplicationDatas.tagListCacheMap == null)
//            ApplicationDatas.tagListCacheMap = new HashMap<>();
//        ApplicationDatas.tagListCacheMap.put(ConstantDatas.HFCT,
//                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.HFCT)));
//        ApplicationDatas.tagListCacheMap.put(ConstantDatas.UW,
//                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.UW)));
//        ApplicationDatas.tagListCacheMap.put(ConstantDatas.TEV,
//                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.TEV)));
//        ApplicationDatas.tagListCacheMap.put(ConstantDatas.INFRARED,
//                new TagListCache(((TagSyncController)controller).loadByType(ConstantDatas.INFRARED)));
//        System.out.println("tagMap's size = " + ApplicationDatas.tagListCacheMap.size() +
//                "\t map has HFCT ? = " + ApplicationDatas.tagListCacheMap.containsKey(ConstantDatas.HFCT));
//        System.out.println(ApplicationDatas.tagListCacheMap.get(ConstantDatas.HFCT).getTagList().size());
//    }

    public static void main(String[] args) {
//        ISyncController controller = new TagSyncController();
//        List<Tag> tags = ((TagSyncController)controller).loadByType(ConstantDatas.HFCT);
//        for (Tag tag : tags) {
//            System.out.println(tag.toString());
//        }
        String json = "{\n" +
                "    \"clientId\": 100126275113511,\n" +
                "    \"datas\": [\n" +
                "        {\n" +
                "            \"pdChartTypeName\": \"\",\n" +
                "            \"file\": \"66974654615143.prpd\",\n" +
                "            \"pdChartTypeId\": \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"type\": \"电缆线路-HFCT\",\n" +
                "    \"url\": \"http://192.168.10.43:8085/biz/app/machineLearn.do?action=analyzeTempFile&fileName=\"\n" +
                "}";
        TrainingRawData rawData = JSONObject.parseObject(json, TrainingRawData.class);
        System.out.println(rawData.toString());
    }
}

package com.jhcomn.lambda.packages.test;

import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.ml_model.MLResultPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户推荐结果数据包
 * Created by shimn on 2017/1/13.
 */
public class UserMLResultPackage extends MLResultPackage {

    // key-value 存储分析结果
    // key = uid, value = "uid1, uid2, ..., uidN"
    private Map<Long, String> results;
//    private long[] uids;
    private String[] datas;

    public UserMLResultPackage(String id) {
        super(id);
        this.setType(PackageType.TEST);
    }

    public UserMLResultPackage(String id, String[] datas) {
        this(id);
        this.datas = datas;
        this.results = transferMatrix2Map(datas);
    }

    private Map<Long,String> transferMatrix2Map(String[] datas) {
        Map<Long, String> maps = new HashMap<>();
        for (int i = 0; i < datas.length; i++) {
            String[] kv = datas[i].split("-");
            if (kv != null && kv.length == 2)
                maps.put(Long.parseLong(kv[0]), kv[1]);
        }
        return maps;
    }

    public String[] getDatas() {
        return datas;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
    }

    public Map<Long, String> getResults() {
        return results;
    }

}

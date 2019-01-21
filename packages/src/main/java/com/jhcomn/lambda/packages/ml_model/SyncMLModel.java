package com.jhcomn.lambda.packages.ml_model;

import com.jhcomn.lambda.packages.IPackage;

/**
 * 机器学习训练同步模型model <--> json格式
 * Created by shimn on 2017/4/24.
 */
public class SyncMLModel implements IPackage {

    //模型全局唯一id（固定，由PackageType写死决定，如：PackageType.HELLO_WORLD.value()）
    public String id;

    //客户全局唯一id
    public String clientId;

    //模型类型-试验类型等
    public String type;

    //最近更新时间
    public String time;

    //以json串为格式的model
    public String model;

    public SyncMLModel() {
    }

    public SyncMLModel(String id, String clientId, String type, String time) {
        this.id = id;
        this.clientId = clientId;
        this.type = type;
        this.time = time;
    }

    public SyncMLModel(String id, String clientId, String type, String time, String model) {
        this.id = id;
        this.clientId = clientId;
        this.type = type;
        this.time = time;
        this.model = model;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"clientId\":" + clientId +
                ",\"type\":\"" + type + '\"' +
                ",\"time\":\"" + time + '\"' +
                ",\"model\":" + model +
                '}';
    }
}

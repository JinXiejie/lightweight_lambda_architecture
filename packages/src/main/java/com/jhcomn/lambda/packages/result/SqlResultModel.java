package com.jhcomn.lambda.packages.result;

import com.jhcomn.lambda.packages.IPackage;

/**
 * mysql的result表(id, clientId, type, datas)
 * 将resultmodel中的datas列表转为json格式的string存储
 * Created by shimn on 2017/5/12.
 */
public class SqlResultModel implements IPackage {
    public String id;
    public String clientId;
    public String type;
    public String datas;

    public SqlResultModel() {
    }

    public SqlResultModel(String id, String clientId, String type, String datas) {
        this.id = id;
        this.clientId = clientId;
        this.type = type;
        this.datas = datas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "SqlResultModel{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", type='" + type + '\'' +
                ", datas='" + datas + '\'' +
                '}';
    }
}

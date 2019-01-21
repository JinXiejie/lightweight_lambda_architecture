package com.jhcomn.lambda.framework.lambda.pkg;

import com.jhcomn.lambda.packages.IPackage;

/**
 * spark-streaming接收的原始数据父类
 * Created by shimn on 2017/5/8.
 */
public class RawData implements IPackage {

    //全局唯一id -- “幂等性”去重
    protected String id;

    //客户id
    protected String clientId;

    //训练集数据类型 -- 可能改为：“设备类型-试验类型”,比如：“开关柜-UW”
    protected String type;

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
}

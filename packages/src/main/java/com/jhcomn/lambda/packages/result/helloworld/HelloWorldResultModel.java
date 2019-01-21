package com.jhcomn.lambda.packages.result.helloworld;

import com.jhcomn.lambda.packages.result.BaseResultModel;

import java.util.List;

/**
 * Created by shimn on 2017/5/25.
 */
public class HelloWorldResultModel extends BaseResultModel {
    private List<HelloWorldProcessResultModel> datas;

    public HelloWorldResultModel() {
    }

    public HelloWorldResultModel(String id, String clientId, String type, List<HelloWorldProcessResultModel> datas) {
        super(id, clientId, type);
        this.datas = datas;
    }

    public List<HelloWorldProcessResultModel> getDatas() {
        return datas;
    }

    public void setDatas(List<HelloWorldProcessResultModel> datas) {
        this.datas = datas;
    }
}

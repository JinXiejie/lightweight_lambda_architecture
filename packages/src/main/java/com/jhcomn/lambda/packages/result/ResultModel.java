package com.jhcomn.lambda.packages.result;


import java.util.List;

/**
 * 结果数据包
 * mysql的result表(id, clientId, type, datas)
 * 必须保证 id 与 input的json id一致
 * Created by shimn on 2017/5/9.
 */
public class ResultModel extends BaseResultModel {

    private List<ProcessResultModel> datas;

    public ResultModel() {
    }

    public ResultModel(String id, String clientId, String type, List<ProcessResultModel> datas) {
        super(id, clientId, type);
        this.datas = datas;
    }

    public List<ProcessResultModel> getDatas() {
        return datas;
    }

    public void setDatas(List<ProcessResultModel> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "clientId='" + clientId + '\'' +
                ", type='" + type + '\'' +
                ", datas=" + datas.size() +
                '}';
    }
}

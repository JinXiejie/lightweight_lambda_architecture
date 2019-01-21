package com.jhcomn.lambda.packages.result.uw1000;

import com.jhcomn.lambda.packages.result.BaseResultModel;

import java.util.List;

/**
 * Created by shimn on 2018/1/22.
 */
public class UWResultModel extends BaseResultModel {

    private List<UWProcessResultModel> datas;

    public UWResultModel() {
    }

    public UWResultModel(String id, String clientId, String type, List<UWProcessResultModel> datas) {
        super(id, clientId, type);
        this.datas = datas;
    }

    public List<UWProcessResultModel> getDatas() {
        return datas;
    }

    public void setDatas(List<UWProcessResultModel> datas) {
        this.datas = datas;
    }
}

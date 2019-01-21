package com.jhcomn.lambda.packages.result.UHF;

import com.jhcomn.lambda.packages.result.BaseResultModel;
import com.jhcomn.lambda.packages.result.UHF.UhfProcessResultModel;

import java.util.List;

public class UhfResultModel extends BaseResultModel {
    private List<UhfProcessResultModel> datas;

    public UhfResultModel() {
    }

    public UhfResultModel(String id, String clientId, String type, List<UhfProcessResultModel> datas) {
        super(id, clientId, type);
        this.datas = datas;
    }

    public List<UhfProcessResultModel> getDatas() {
        return datas;
    }

    public void setDatas(List<UhfProcessResultModel> datas) {
        this.datas = datas;
    }
}

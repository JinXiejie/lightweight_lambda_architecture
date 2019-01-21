package com.jhcomn.lambda.packages.result.hfct1500;

import com.jhcomn.lambda.packages.result.BaseResultModel;

import java.util.List;

/**
 * Created by shimn on 2017/5/25.
 */
public class HfctResultModel extends BaseResultModel {

    private List<HfctProcessResultModel> datas;

    public HfctResultModel() {
    }

    public HfctResultModel(String id, String clientId, String type, List<HfctProcessResultModel> datas) {
        super(id, clientId, type);
        this.datas = datas;
    }

    public List<HfctProcessResultModel> getDatas() {
        return datas;
    }

    public void setDatas(List<HfctProcessResultModel> datas) {
        this.datas = datas;
    }
}

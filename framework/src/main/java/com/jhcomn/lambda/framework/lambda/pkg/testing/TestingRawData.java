package com.jhcomn.lambda.framework.lambda.pkg.testing;

import com.jhcomn.lambda.framework.lambda.pkg.RawData;

import java.util.List;

/**
 * 智能分析原始数据，来自web
 * Created by shimn on 2017/5/8.
 */
public class TestingRawData extends RawData {

    //测试集数据远端文件夹路径
    private String url;

    //原始数据信息
    private List<TestingData> datas;

    public List<TestingData> getDatas() {
        return datas;
    }

    public void setDatas(List<TestingData> datas) {
        this.datas = datas;
    }

    public String getDataStr() {
        if (datas == null) return null;
        StringBuilder ret = new StringBuilder();
        for (TestingData data : datas) {
            ret.append(data.toString() + "\n");
        }
        return ret.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TestingRawData{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", type='" + type + '\'' +
                "datas=" + datas +
                '}';
    }
}

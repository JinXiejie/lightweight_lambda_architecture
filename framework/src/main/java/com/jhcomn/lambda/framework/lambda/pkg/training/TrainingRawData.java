package com.jhcomn.lambda.framework.lambda.pkg.training;

import com.jhcomn.lambda.framework.lambda.pkg.RawData;

import java.io.Serializable;
import java.util.List;

/**
 * 第一次学习or再学习原始数据，来自web
 * Created by shimn on 2016/12/20.
 */
public class TrainingRawData extends RawData {

    //训练集数据远端文件夹路径
    private String url;

    //原始数据信息
    private List<TrainingData> datas;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TrainingData> getDatas() {
        return datas;
    }

    public String getDataStr() {
        if (datas == null) return null;
        StringBuilder ret = new StringBuilder();
        for (TrainingData data : datas) {
            ret.append(data.toString() + "\n");
        }
        return ret.toString();
    }

    public void setDatas(List<TrainingData> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "TrainingRawData{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", datas=" + datas +
                '}';
    }
}

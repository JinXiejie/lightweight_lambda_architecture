package com.jhcomn.lambda.framework.lambda.pkg.tags;

import com.jhcomn.lambda.packages.AbstractPackage;

import java.util.List;

/**
 * {
 *     "id":1,
 *     "datas":[TagsTypeData]
 * }
 * Created by shimn on 2017/5/18.
 */
public class TagsRawData extends AbstractPackage{

    private List<TagsTypeData> datas;

    public List<TagsTypeData> getDatas() {
        return datas;
    }

    public void setDatas(List<TagsTypeData> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "TagsRawData{" +
                "datas=" + datas +
                '}';
    }
}

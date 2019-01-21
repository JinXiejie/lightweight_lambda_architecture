package com.jhcomn.lambda.framework.lambda.pkg.cache;


import com.jhcomn.lambda.packages.AbstractPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * HDFS需要拉取文件信息包汇总包
 * Created by shimn on 2016/12/26.
 */
public class HdfsFetchPkg extends AbstractPackage {

    protected String date;

    //key有三种：1）训练=>train；2）分类=>test；3）类别清单=>taglist
    protected String key;

    //type则为试验类型
    protected String type;

    public HdfsFetchPkg(String key, String date) {
        this.key = key;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HdfsFetchPkg{" +
                "date='" + date + '\'' +
                "key='" + key + '\'' +
                '}';
    }
}

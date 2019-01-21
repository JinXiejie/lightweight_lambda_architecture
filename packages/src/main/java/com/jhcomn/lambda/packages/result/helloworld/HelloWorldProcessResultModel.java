package com.jhcomn.lambda.packages.result.helloworld;

import com.jhcomn.lambda.packages.result.ProcessResultModel;

/**
 * Created by shimn on 2017/5/25.
 */
public class HelloWorldProcessResultModel extends ProcessResultModel {

    private String tag;

    public HelloWorldProcessResultModel() {
    }

    public HelloWorldProcessResultModel(String file, String tag) {
        super(file);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

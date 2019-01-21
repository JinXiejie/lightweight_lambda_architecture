package com.jhcomn.lambda.mllib.base.adapter;

import com.jhcomn.lambda.packages.common.ApplicationDatas;

import java.io.Serializable;

/**
 * Created by shimn on 2018/1/9.
 */
public class UwTagAdapter extends BaseTagAdapter implements Serializable {
    public UwTagAdapter() {
        update(ApplicationDatas.UW);
    }

    public void update() {
        super.update(ApplicationDatas.UW);
    }
}

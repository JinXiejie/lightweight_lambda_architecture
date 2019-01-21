package com.jhcomn.lambda.mllib.base.adapter;

import com.jhcomn.lambda.packages.common.ApplicationDatas;

import java.io.Serializable;

public class UhfTagAdapter extends BaseTagAdapter implements Serializable {
    public UhfTagAdapter() {
        update(ApplicationDatas.UHF);
    }

    public void update() {
        super.update(ApplicationDatas.UHF);
    }
}

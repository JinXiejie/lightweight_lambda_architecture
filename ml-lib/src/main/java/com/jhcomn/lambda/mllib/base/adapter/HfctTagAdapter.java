package com.jhcomn.lambda.mllib.base.adapter;


import com.jhcomn.lambda.packages.common.ApplicationDatas;
import com.jhcomn.lambda.packages.tag.Tag;

import java.io.Serializable;

/**
 * tagId <--> tag 转接器
 * 1) 结果tag(index) --> tagId
 * 2) 训练tagId --> tag
 * Created by shimn on 2017/5/22.
 */
public class HfctTagAdapter extends BaseTagAdapter implements Serializable {

    public HfctTagAdapter() {
        update(ApplicationDatas.HFCT);
    }

    public void update() {
        super.update(ApplicationDatas.HFCT);
    }

}

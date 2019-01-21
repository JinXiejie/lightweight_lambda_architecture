package com.jhcomn.lambda.framework.lambda.pkg.batch;

import com.jhcomn.lambda.framework.lambda.pkg.AbstractUpdatePkg;

/**
 * 全量数据定时更新package
 * Created by shimn on 2016/12/30.
 */
public class BatchUpdatePkg extends AbstractUpdatePkg {

    private String uid;

    public BatchUpdatePkg(String uid, String type) {
        super(type);
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}

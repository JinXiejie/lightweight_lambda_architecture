package com.jhcomn.lambda.packages.ml_model;

import com.jhcomn.lambda.packages.AbstractPackage;
import com.jhcomn.lambda.packages.PackageType;

/**
 * 机器学习结果包父类
 * Created by shimn on 2017/1/13.
 */
public class MLResultPackage extends AbstractPackage {

    private PackageType type;

    public MLResultPackage(String id) {
        super(id);
    }

    public MLResultPackage(PackageType type, String id) {
        super(id);
        this.type = type;
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

}

package com.jhcomn.lambda.framework.lambda.persistence.client;

import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.ml_model.MLResultPackage;

/**
 * Created by shimn on 2017/1/16.
 */
public abstract class AbstractDaoClient implements IDaoClient {

    protected PackageType pkgType;

    public AbstractDaoClient() {
        init();
    }

    @Override
    public void save(IPackage pkg) {
        MLResultPackage mlResultPkg = (MLResultPackage) pkg;
        pkgType = mlResultPkg.getType();
    }
}

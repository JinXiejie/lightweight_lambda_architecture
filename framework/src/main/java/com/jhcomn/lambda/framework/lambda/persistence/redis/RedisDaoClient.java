package com.jhcomn.lambda.framework.lambda.persistence.redis;

import com.jhcomn.lambda.framework.lambda.persistence.client.AbstractDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.client.IDaoClient;
import com.jhcomn.lambda.packages.IPackage;

/**
 * TODO Redis DAO 操作
 * Created by shimn on 2017/1/16.
 */
public class RedisDaoClient extends AbstractDaoClient {
    @Override
    public void init() {

    }

    @Override
    public void save(IPackage pkg) {
        super.save(pkg);
    }

    @Override
    public void delete(IPackage pkg) {

    }

    @Override
    public void close() {

    }
}

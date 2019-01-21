package com.jhcomn.lambda.framework.lambda.persistence.client;

import com.jhcomn.lambda.packages.IPackage;

/**
 * Created by shimn on 2017/1/13.
 */
public interface IDaoClient {

    /**
     * 创建DAO连接
     */
    void init();
    /**
     * 持久化到NoSQL
     * @param pkg
     */
    void save(IPackage pkg);

    /**
     * 从NoSQL删除数据
     * @param pkg
     */
    void delete(IPackage pkg);

    /**
     * 关闭数据库连接资源
     */
    void close();
}

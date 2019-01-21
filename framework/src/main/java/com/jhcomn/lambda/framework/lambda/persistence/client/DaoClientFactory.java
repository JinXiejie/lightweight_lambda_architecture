package com.jhcomn.lambda.framework.lambda.persistence.client;

import com.jhcomn.lambda.framework.lambda.persistence.hbase.HBaseDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.redis.RedisDaoClient;

/**
 * DAO连接工厂
 * Created by shimn on 2017/1/16.
 */
public class DaoClientFactory {

    /**
     * 创建HBase DAO连接
     * @return
     */
    public static HBaseDaoClient createHBaseDaoClient() {
        return new HBaseDaoClient();
    }

    /**
     * 创建Redis DAO连接
     * @return
     */
    public static RedisDaoClient createRedisDaoClient() {
        return new RedisDaoClient();
    }

}

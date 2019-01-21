package com.jhcomn.lambda.framework.lambda.sync.base;

import com.jhcomn.lambda.packages.IPackage;

/**
 * 同步机器学习模型的接口
 * 增删改查
 * Created by shimn on 2017/4/24.
 */
public interface ISyncController {

    //创建唯一id,持久化到mysql
    void saveOrUpdate(IPackage model);

    //根据唯一id删除
    void delete(String id);

    //根据唯一id加载模型
    IPackage load(String id);

}

package com.jhcomn.lambda.framework.lambda.sync.tag.dispatch;

import com.jhcomn.lambda.packages.IPackage;

/**
 * Created by shimn on 2017/5/22.
 */
public interface ITagSyncDispatcher {

    //根据试验类型分发，然后转成各算法层需要的tag形式，存储到mysql的tag表
    void dispatch(IPackage pkg);

}

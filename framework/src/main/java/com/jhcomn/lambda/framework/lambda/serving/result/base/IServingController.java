package com.jhcomn.lambda.framework.lambda.serving.result.base;

import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.packages.IPackage;

/**
 * 结果层统一接口
 * 1、持久化结果到mysql的result表(id, clientId, type, datas)
 * 2、发送结果json到kafka的broker，供web的consumer消费
 * Created by shimn on 2017/5/10.
 */
public interface IServingController extends ISyncController {

    //发送json结果到kafka
    void send(IPackage model);

}

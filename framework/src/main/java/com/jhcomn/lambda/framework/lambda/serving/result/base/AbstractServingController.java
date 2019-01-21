package com.jhcomn.lambda.framework.lambda.serving.result.base;

import com.jhcomn.lambda.framework.lambda.serving.result.mq.ProducerManager;
import com.jhcomn.lambda.framework.lambda.serving.result.mq.ServingProducer;
import com.jhcomn.lambda.framework.lambda.sync.base.AbstractSyncController;

/**
 * 结果层控制器父类
 * Created by shimn on 2017/5/10.
 */
public abstract class AbstractServingController extends AbstractSyncController implements IServingController {

    protected ServingProducer producer = null;

    public AbstractServingController() {
        super();
        producer = ProducerManager.getProducer();
    }

    @Override
    protected void close() {
        super.close();
        ProducerManager.release();
    }
}

package com.jhcomn.lambda.framework.lambda.serving;

import com.jhcomn.lambda.framework.lambda.base.AbstractSparkLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 对外服务层
 * RESTful风格
 * Created by shimn on 2017/1/13.
 */
public class ServingLayer extends AbstractSparkLayer {

    private static final Logger log = LoggerFactory.getLogger(ServingLayer.class);

    protected ServingLayer(Properties props) {
        super(props);
    }

    @Override
    protected String getConfigGroup() {
        return "com.jhcomn.lambda.app.serving";
    }

    @Override
    protected String getLayerName() {
        return "ServingLayer";
    }

    @Override
    protected void buildDataConnector() {

    }

    @Override
    public void run() {

    }
}

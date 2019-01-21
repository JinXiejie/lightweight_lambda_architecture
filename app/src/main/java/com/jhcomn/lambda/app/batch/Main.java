package com.jhcomn.lambda.app.batch;

import com.jhcomn.lambda.app.utils.PropertyUtil;
import com.jhcomn.lambda.framework.lambda.batch.BatchLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by shimn on 2016/12/2.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Properties props = PropertyUtil.readProperty("app.properties");
        try {
            BatchLayer batchLayer = new BatchLayer(props);
            batchLayer.start();
//            batchLayer.run();
//            batchLayer.await();
        } catch (Exception e) {
            log.error("Batch Layer Main error = " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.jhcomn.lambda.app.speed;

import com.jhcomn.lambda.app.utils.PropertyUtil;
import com.jhcomn.lambda.framework.lambda.speed.SpeedLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by shimn on 2016/12/30.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Properties props = PropertyUtil.readProperty("app.properties");
        try {
            SpeedLayer speedLayer = new SpeedLayer(props);
            speedLayer.start();
            speedLayer.await();
        } catch (InterruptedException e) {
            log.error("Speed Layer Main error = " + e.getMessage());
            e.printStackTrace();
        }
    }
}

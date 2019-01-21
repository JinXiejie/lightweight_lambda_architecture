package com.jhcomn.lambda.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * 读取属性文件.properties
 * Created by shimn on 2016/12/1.
 */
public class PropertyUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertyUtil.class);

    public static Properties readProperty(String file) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in =  PropertyUtil.class.getClassLoader().getResourceAsStream(file);
            props.load(in);
        } catch (IOException e) {
            log.error(e.toString());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
        return props;
    }

//    public static void main(String[] args) {
//        Properties props = readProperty("cluster.properties");
//        Iterator<String> it = props.stringPropertyNames().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            System.out.println(key+":" + props.getProperty(key));
//        }
//    }
}

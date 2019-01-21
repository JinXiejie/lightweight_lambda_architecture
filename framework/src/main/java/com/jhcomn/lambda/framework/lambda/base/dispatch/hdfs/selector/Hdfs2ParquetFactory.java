package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector;

import com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers.*;
import com.jhcomn.lambda.packages.PackageType;

import java.util.HashMap;
import java.util.Map;

/**
 * HDFS策略工厂
 * Created by shimn on 2016/12/27.
 */
public class Hdfs2ParquetFactory {

    private static volatile Hdfs2ParquetFactory factory = new Hdfs2ParquetFactory();

    private Hdfs2ParquetFactory() {}

    public static Hdfs2ParquetFactory getInstance() {
        return factory;
    }

    private static Map maps = new HashMap<>();

    //注册业务策略
    static {
        maps.put(PackageType.TEST.value(), new TestDispatcher());
        maps.put(PackageType.UW.value(), new UwDispatcher());
        maps.put(PackageType.TEV.value(), new TevDispatcher());
        maps.put(PackageType.INFRARED.value(), new InfraredDispatcher());
        maps.put(PackageType.HFCT.value(), new HfctDispatcher());
        maps.put(PackageType.HELLO_WORLD.value(), new HelloWorldDispatcher());
        maps.put(PackageType.UHF.value(), new UhfDispatcher());
    }

    public IHdfs2Parquet create(Integer type) {
        System.out.println("Dispatcher create now.");
        return (IHdfs2Parquet) maps.get(type);
    }
}

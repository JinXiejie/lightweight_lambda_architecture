package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector;

import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;

/**
 * HDFS策略上下文
 * 供外部调用
 * Created by shimn on 2016/12/27.
 */
public class Hdfs2ParquetContext {

    private IHdfs2Parquet strategy;

    public Hdfs2ParquetContext() {
    }

    public void saveAndUpdate(String date, IPackage pkg, Integer type, boolean isSpeed, String key) {
        strategy = Hdfs2ParquetFactory.getInstance().create(type);
        System.out.println("Dispatcher is : " + strategy.getClass());
        strategy.saveAndUpdate(date, pkg, PackageType.valueOfKey(type), isSpeed, key);
    }

    public IHdfs2Parquet getStrategy() {
        return strategy;
    }

    public void setStrategy(IHdfs2Parquet strategy) {
        this.strategy = strategy;
    }
}

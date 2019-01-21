package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector;

import com.jhcomn.lambda.framework.lambda.base.common.strategy.IStrategy;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;

/**
 * Hdfs 分别存储数据包策略行为
 * Created by shimn on 2016/12/27.
 */
public interface IHdfs2Parquet extends IStrategy {

    /**
     *
     * @param date
     * @param ipkg
     * @param type
     * @param isSpeed   区分批处理和流计算
     * @param key   区分训练or测试
     */
    void saveAndUpdate(String date, IPackage ipkg, PackageType type, boolean isSpeed, String key);

}

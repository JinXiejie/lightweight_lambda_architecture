package com.jhcomn.lambda.framework.lambda.batch.persistence.hdfs;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.Hdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.packages.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch Layer Hdfs文本文件转存为Parquet --> 定时转换存储为Parquet --> ../uid/type/table/key=batch
 * Created by shimn on 2016/12/30.
 */
public class BatchHdfs2ParquetWrapper extends Hdfs2ParquetWrapper {

    private static Logger log = LoggerFactory.getLogger(BatchHdfs2ParquetWrapper.class);

    private BatchUpdatePkg batchUpdatePkg;

    public BatchHdfs2ParquetWrapper(String date, BatchUpdatePkg pkg) {
        super(date);
        this.batchUpdatePkg = pkg;
    }

    @Override
    public void dispatch() {
        if (batchUpdatePkg != null && parquetContext != null) {
            String type = batchUpdatePkg.getType();
            if (type.equals(ConstantDatas.UW)) {
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.UW.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.TEV)) {
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.TEV.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.INFRARED)) {
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.INFRARED.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.HFCT)) {
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.HFCT.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.TEST)) {
                System.out.println("BatchHdfs2ParquetWrapper saveOrUpdate&update topic TEST now.");
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.TEST.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.HELLO_WORLD)) {
                System.out.println("BatchHdfs2ParquetWrapper saveOrUpdate&update topic HELLO_WORLD now.");
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.HELLO_WORLD.value(), false, ConstantDatas.TRAINING);
            }
            else if (type.equals(ConstantDatas.UHF)) {
                System.out.println("BatchHdfs2ParquetWrapper saveOrUpdate&update topic UHF now.");
                parquetContext.saveAndUpdate(date, batchUpdatePkg, PackageType.UHF.value(), false, ConstantDatas.TRAINING);
            }
        }
        else {
            log.error("metaPkg or parquetContext is null...");
        }
    }
}

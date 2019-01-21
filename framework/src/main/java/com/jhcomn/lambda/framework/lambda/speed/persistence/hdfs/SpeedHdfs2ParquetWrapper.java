package com.jhcomn.lambda.framework.lambda.speed.persistence.hdfs;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.Hdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Speed Layer Hdfs文本文件转存为Parquet --> 实时转换存储为Parquet --> ../uid/type/table/key=speed
 * Created by shimn on 2016/12/30.
 */
public class SpeedHdfs2ParquetWrapper extends Hdfs2ParquetWrapper {

    private static Logger log = LoggerFactory.getLogger(SpeedHdfs2ParquetWrapper.class);

    private HdfsFetchTTMetaPkg metaPkg;

    private String key; //分为训练Train和测试Test

    public SpeedHdfs2ParquetWrapper(String date) {
        super(date);
    }

    public HdfsFetchTTMetaPkg getMetaPkg() {
        return metaPkg;
    }

    public void setMetaPkg(IPackage pkg) {
        this.metaPkg = (HdfsFetchTTMetaPkg) pkg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void dispatch() {
        if (metaPkg != null && parquetContext != null) {
            String type = metaPkg.getType();
            if (type.equals(ConstantDatas.UW)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.UW.value(), true, key);
            }
            else if (type.equals(ConstantDatas.TEV)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.TEV.value(), true, key);
            }
            else if (type.equals(ConstantDatas.INFRARED)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.INFRARED.value(), true, key);
            }
            else if (type.equals(ConstantDatas.HFCT)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.HFCT.value(), true, key);
            }
            else if (type.equals(ConstantDatas.TEST)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.TEST.value(), true, key);
            }
            else if (type.equals(ConstantDatas.HELLO_WORLD)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.HELLO_WORLD.value(), true, key);
            }
            else if (type.equals(ConstantDatas.UHF)) {
                parquetContext.saveAndUpdate(date, metaPkg, PackageType.UHF.value(), true, key);
            }
        }
        else {
            log.error("metaPkg or parquetContext is null...");
        }
    }
}

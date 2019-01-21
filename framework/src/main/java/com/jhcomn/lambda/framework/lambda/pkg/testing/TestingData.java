package com.jhcomn.lambda.framework.lambda.pkg.testing;

import com.jhcomn.lambda.packages.IPackage;

/**
 * 测试单元数据
 * Created by shimn on 2017/5/8.
 */
public class TestingData implements IPackage {

    //数据文件名称
    protected String file;

    protected String pdChartTypeId;

    protected String pdChartTypeName;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPdChartTypeId() {
        return pdChartTypeId;
    }

    public void setPdChartTypeId(String pdChartTypeId) {
        this.pdChartTypeId = pdChartTypeId;
    }

    public String getPdChartTypeName() {
        return pdChartTypeName;
    }

    public void setPdChartTypeName(String pdChartTypeName) {
        this.pdChartTypeName = pdChartTypeName;
    }

    @Override
    public String toString() {
        return "TestingData{" +
                "file='" + file + '\'' +
                ", pdChartTypeId='" + pdChartTypeId + '\'' +
                ", pdChartTypeName='" + pdChartTypeName + '\'' +
                '}';
    }
}

package com.jhcomn.lambda.framework.lambda.pkg;

import com.jhcomn.lambda.packages.AbstractPackage;

/**
 * Created by shimn on 2016/12/30.
 */
public abstract class AbstractUpdatePkg extends AbstractPackage {

    protected String type; //试验类型

    protected String device; //设备名称

//    protected String chart; //图谱名称

    public AbstractUpdatePkg(String type) {
        //TODO type可能是“试验类型-图谱类型”or“设备类型-试验类型-图谱类型”
        if (type != null) {
            String[] arr = type.split("-");
            if (arr.length < 2) {
                //两种情况：1）是数据包，则type字段确实缺失信息；2）是批处理包BatchUpdatePkg，则type=topic
                this.type = type;
                if (arr.length != 1) {
                    System.out.println("type字段有错误");
                }
            }
            else {
                this.device = arr[0];
                this.type = arr[1];
//                this.chart = arr[2];
            }
        }
        else {
            this.type = type;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

//    public String getChart() {
//        return chart;
//    }
//
//    public void setChart(String chart) {
//        this.chart = chart;
//    }

    @Override
    public String toString() {
        return "AbstractUpdatePkg{" +
                "type='" + type + '\'' +
                ", device='" + device + '\'' +
//                ", chart='" + chart + '\'' +
                '}';
    }
}

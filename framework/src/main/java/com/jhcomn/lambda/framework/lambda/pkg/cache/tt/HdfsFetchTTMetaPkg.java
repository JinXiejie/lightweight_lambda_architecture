package com.jhcomn.lambda.framework.lambda.pkg.cache.tt;

import com.jhcomn.lambda.framework.lambda.pkg.AbstractUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.training.TrainingData;
import com.jhcomn.lambda.packages.IPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * HDFS需要拉取文件信息包 --> train & test
 * Created by shimn on 2016/12/26.
 */
public class HdfsFetchTTMetaPkg extends AbstractUpdatePkg {

    private String uid;

    private String url;

    private List<TrainingData> files;

    public HdfsFetchTTMetaPkg(String uid, String type, String url) {
        super(type);
        this.uid = uid;
        this.url = url;
        files = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TrainingData> getFiles() {
        return files;
    }

    public void setFiles(List<TrainingData> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "HdfsFetchTTMetaPkg{" +
                "uid=" + uid +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", files=" + files +
                '}';
    }
}

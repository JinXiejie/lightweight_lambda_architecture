package com.jhcomn.lambda.packages.result;

import com.jhcomn.lambda.packages.IPackage;

/**
 * Created by shimn on 2017/5/9.
 */
public class ProcessResultModel implements IPackage {
    private String file;

    public ProcessResultModel() {
    }

    public ProcessResultModel(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}

package com.jhcomn.lambda.packages;

/**
 * Created by shimn on 2016/12/26.
 */
public class AbstractPackage implements IPackage {
    protected String id;

    public AbstractPackage() {
    }

    public AbstractPackage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

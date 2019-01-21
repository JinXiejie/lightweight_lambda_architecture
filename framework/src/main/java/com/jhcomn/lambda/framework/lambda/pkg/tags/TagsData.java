package com.jhcomn.lambda.framework.lambda.pkg.tags;

import com.jhcomn.lambda.packages.IPackage;

/**
 * {
 "id": 1,
 "name": "内部放电"
 }

 * Created by shimn on 2017/5/18.
 */
public class TagsData implements IPackage {

    private String id;

    private String name;

    public TagsData() {
    }

    public TagsData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagsData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

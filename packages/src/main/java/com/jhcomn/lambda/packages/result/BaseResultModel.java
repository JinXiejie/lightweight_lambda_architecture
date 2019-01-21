package com.jhcomn.lambda.packages.result;

import com.jhcomn.lambda.packages.AbstractPackage;

/**
 * Created by shimn on 2017/5/12.
 */
public class BaseResultModel extends AbstractPackage {

    protected String clientId;

    protected String type;

    public BaseResultModel() {
    }

    public BaseResultModel(String id, String clientId, String type) {
        super(id);
        this.clientId = clientId;
        this.type = type;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

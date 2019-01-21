package com.jhcomn.lambda.framework.lambda.pkg.cache.tt;

import com.jhcomn.lambda.framework.lambda.pkg.cache.HdfsFetchPkg;

import java.util.ArrayList;
import java.util.List;

/**
 * train & test
 * Created by shimn on 2017/5/18.
 */
public class HdfsFetchTTPkg extends HdfsFetchPkg {

    private List<HdfsFetchTTMetaPkg> metaPkgs;

    public HdfsFetchTTPkg(String key, String date) {
        super(key, date);
        metaPkgs = new ArrayList<>();
    }

    public List<HdfsFetchTTMetaPkg> getMetaPkgs() {
        return metaPkgs;
    }

    public void setMetaPkgs(List<HdfsFetchTTMetaPkg> metaPkgs) {
        this.metaPkgs = metaPkgs;
    }

    @Override
    public String toString() {
        return "HdfsFetchTTPkg{" +
                "metaPkgs=" + metaPkgs +
                '}';
    }
}

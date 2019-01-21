package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;

/**
 * Created by shimn on 2016/12/27.
 */
public class TevDispatcher extends BaseDispatcher {

    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {

    }

    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {

    }

    @Override
    protected void analyze(String date, HdfsFetchTTMetaPkg metaPkg) {

    }
}

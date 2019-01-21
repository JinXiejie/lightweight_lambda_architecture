package com.jhcomn.lambda.framework.lambda.base.dispatch.dispatchers;

import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.pkg.batch.BatchUpdatePkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;

/**
 * Created by shimn on 2016/12/27.
 */
public class InfraredDispatcher extends BaseDispatcher {

    @Override
    protected void incrementalCompute(String date, HdfsFetchTTMetaPkg metaPkg) {
        System.out.println("speed layer start incremental computation, date is " + date);

        String path = Properties.ROOT_DATA_PATH
                + "/" + metaPkg.getUid()
                + "/" + metaPkg.getType()
                + Properties.HDFS_RAW_DATA_PATH;

        validate(path);

    }

    @Override
    protected void fullCompute(String date, BatchUpdatePkg ipkg) {

    }

    @Override
    protected void analyze(String date, HdfsFetchTTMetaPkg metaPkg) {

    }
}

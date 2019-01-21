package com.jhcomn.lambda.framework.lambda.persistence.hdfs;

import com.jhcomn.lambda.framework.lambda.persistence.client.AbstractDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.client.IDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadOnlyHdfsDao;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadWriteHdfsDao;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadOnlyHdfsDaoImpl;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadWriteHdfsDaoImpl;
import com.jhcomn.lambda.packages.IPackage;
import org.apache.hadoop.fs.FileSystem;

/**
 * HDFS DAO 客户端
 * Created by shimn on 2017/1/16.
 */
public class HdfsDaoClient extends AbstractDaoClient {

    private FileSystem fileSystem = null;

    private ReadOnlyHdfsDao readOnlyHdfsDao = null;
    private ReadWriteHdfsDao readWriteHdfsDao = null;

    public HdfsDaoClient(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        init();
    }

    @Override
    public void init() {
        if (fileSystem != null) {
            readOnlyHdfsDao = new ReadOnlyHdfsDaoImpl(fileSystem);
            readWriteHdfsDao = new ReadWriteHdfsDaoImpl(fileSystem, readOnlyHdfsDao);
        }
    }

    @Override
    public void save(IPackage pkg) {

    }

    @Override
    public void delete(IPackage pkg) {

    }

    @Override
    public void close() {
        if (readWriteHdfsDao != null)
            readWriteHdfsDao.close();
    }
}

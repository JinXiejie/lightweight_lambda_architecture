package com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.HdfsDao;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Hdfs Dao 基类
 * Created by shimn on 2016/12/19.
 */
public class HdfsDaoImpl implements HdfsDao {

    private static final Logger log = LoggerFactory.getLogger(HdfsDaoImpl.class);
    protected final FileSystem fileSystem;

    public HdfsDaoImpl(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public boolean validate() {
        if (this.fileSystem == null) {
            log.error("HDFS fileSystem is null !!!");
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {
        if (validate()) {
            try {
                fileSystem.close();
                return true;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }
}

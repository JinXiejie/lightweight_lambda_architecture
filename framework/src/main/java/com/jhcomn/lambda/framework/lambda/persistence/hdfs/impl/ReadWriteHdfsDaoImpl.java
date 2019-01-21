package com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataInputStreamCallback;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataOutputStreamCallback;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadOnlyHdfsDao;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadWriteHdfsDao;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.util.Progressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 读+写HDFS操作实现类
 * Created by shimn on 2016/12/19.
 */
public class ReadWriteHdfsDaoImpl extends HdfsDaoImpl implements ReadWriteHdfsDao {

    private static final Logger log = LoggerFactory.getLogger(ReadWriteHdfsDaoImpl.class);

    protected ReadOnlyHdfsDao readOnlyHdfsDao = null;

    public ReadWriteHdfsDaoImpl(FileSystem fileSystem, ReadOnlyHdfsDao dao) {
        super(fileSystem);
        if (dao == null) {
            throw new RuntimeException("ReadOnlyHdfsDao should not be null.");
        }
        this.readOnlyHdfsDao = dao;
    }

    @Override
    public void delete(String path) {
        if (validate()) {
            try {
                fileSystem.delete(new Path(path), true); //递归删除文件夹及文件夹中的文件
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void mkdirs(String path) {
        if (validate()) {
            try {
                fileSystem.mkdirs(new Path(path));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void write(String path, String contents) {
        write(path, new FSDataOutputStreamCallback() {
            @Override
            public void serialize(FSDataOutputStream fos) {
                try {
                    IOUtils.write(contents, fos);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }

    @Override
    public void write(String path, FSDataOutputStreamCallback serializer) {
        FSDataOutputStream fos = null;
        try {
            fos = fileSystem.create(new Path(path), true);
            serializer.serialize(fos);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    public void copyFromLocalFile(Path src, Path dst) {
        if (validate()) {
            try {
                fileSystem.copyFromLocalFile(src, dst);
            } catch (IOException e) {
                log.error(e.getMessage());
                System.out.println(e.toString());
            }
        }
    }

    @Override
    public int uploadWithStream(InputStream inStream, String path) throws IOException {
        try {
            OutputStream outStream = fileSystem.create(new Path(path), new Progressable() {
                public void progress() {
                    System.out.print('.');
                }
            });
            org.apache.hadoop.io.IOUtils.copyBytes(inStream, outStream, 4096, true);
            inStream.close();
            return 0;
        } catch (IOException e) {
            inStream.close();
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public FSDataOutputStream create(Path file) {
        FSDataOutputStream fos = null;
        try {
            fos = fileSystem.create(file, true);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return fos;
    }

    @Override
    public void rename(Path src, Path dst) {
        if (validate()) {
            try {
                fileSystem.rename(src, dst);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public FileStatus[] globStatus(String path) {
        return readOnlyHdfsDao.globStatus(path);
    }

    @Override
    public FileStatus[] listStatus(String path) {
        return readOnlyHdfsDao.listStatus(path);
    }

    @Override
    public FileStatus getFileStatus(String path) {
        return readOnlyHdfsDao.getFileStatus(path);
    }

    @Override
    public ContentSummary getTotalSummary(String path) {
        return readOnlyHdfsDao.getTotalSummary(path);
    }

    @Override
    public List<String> readFile(String filePath) {
        return readOnlyHdfsDao.readFile(filePath);
    }

    @Override
    public <T> T readFile(String filePath, FSDataInputStreamCallback<T> deserializer) {
        return readOnlyHdfsDao.readFile(filePath, deserializer);
    }

    @Override
    public boolean exists(String path) {
        return readOnlyHdfsDao.exists(path);
    }

    @Override
    public void download(Path remote, Path local) {
        readOnlyHdfsDao.download(remote, local);
    }

    @Override
    public long getLastModifiedTime(Path file) {
        return readOnlyHdfsDao.getLastModifiedTime(file);
    }

    @Override
    public Path[] listAllFile(Path dir) {
        return readOnlyHdfsDao.listAllFile(dir);
    }

    @Override
    public BlockLocation[] location(Path file) {
        return readOnlyHdfsDao.location(file);
    }

    @Override
    public DatanodeInfo[] getClusterNodes() {
        return readOnlyHdfsDao.getClusterNodes();
    }
}

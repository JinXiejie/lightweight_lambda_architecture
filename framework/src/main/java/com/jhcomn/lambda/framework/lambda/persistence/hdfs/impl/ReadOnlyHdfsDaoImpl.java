package com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataInputStreamCallback;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadOnlyHdfsDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 只读HDFS DAO操作实现类
 * Created by shimn on 2016/12/19.
 */
public class ReadOnlyHdfsDaoImpl extends HdfsDaoImpl implements ReadOnlyHdfsDao {

    private static final Logger log = LoggerFactory.getLogger(ReadOnlyHdfsDaoImpl.class);

    public ReadOnlyHdfsDaoImpl(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public FileStatus[] globStatus(String path) {
        if (validate()) {
            try {
                return fileSystem.globStatus(new Path(path));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public FileStatus[] listStatus(String path) {
        if (validate()) {
            try {
                return fileSystem.listStatus(new Path(path));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public FileStatus getFileStatus(String path) {
        if (validate()) {
            try {
                return fileSystem.getFileStatus(new Path(path));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public ContentSummary getTotalSummary(String path) {
        if (validate()) {
            try {
                final FileStatus[] fileStatuses = fileSystem.globStatus(new Path(path));
                long dirCount = 0, fileCount = 0, length = 0;
                if (ArrayUtils.isEmpty(fileStatuses)) {
                    return new ContentSummary(length, fileCount, dirCount);
                }

                for (final FileStatus fileStatus : fileStatuses) {
                    final ContentSummary summary = fileSystem.getContentSummary(fileStatus.getPath());
                    dirCount += summary.getDirectoryCount();
                    fileCount += summary.getFileCount();
                    length += summary.getLength();
                }
                return new ContentSummary(length, fileCount, dirCount);
            } catch (final IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<String> readFile(String filePath) {
        if (validate()) {
            return readFile(filePath, new FSDataInputStreamCallback<List<String>>() {
                @Override
                public List<String> deserialize(FSDataInputStream fsis) {
                    try {
                        return IOUtils.readLines(fsis);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    return null;
                }
            });
        }
        return null;
    }

    @Override
    public <T> T readFile(String filePath, FSDataInputStreamCallback<T> deserializer) {
        if (validate()) {
            FSDataInputStream fsis = null;
            try {
                fsis = fileSystem.open(new Path(filePath));
                return deserializer.deserialize(fsis);
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                IOUtils.closeQuietly(fsis);
            }
        }
        return null;
    }

    @Override
    public boolean exists(String path) {
        if (validate()) {
            try {
                return fileSystem.exists(new Path(path));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void download(Path remote, Path local) {
        if (validate()) {
            try {
                fileSystem.copyToLocalFile(remote, local);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.toString());
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public long getLastModifiedTime(Path file) {
        if (validate()) {
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(file);
                return fileStatus.getModificationTime();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return -1L;
    }

    @Override
    public Path[] listAllFile(Path dir) {
        if (validate()) {
            try {
                FileStatus[] stats = fileSystem.listStatus(dir);
                Path[] paths = new Path[stats.length];
                for (int i = 0; i < stats.length; i++) {
                    paths[i] = stats[i].getPath();
                }
                return paths;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public BlockLocation[] location(Path file) {
        if (validate()) {
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(file);
                return fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public DatanodeInfo[] getClusterNodes() {
        if (validate()) {
            try {
                DistributedFileSystem dfs = (DistributedFileSystem) fileSystem;
                return dfs.getDataNodeStats();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

}

package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.base.common.constants.Properties;
import com.jhcomn.lambda.framework.lambda.base.common.observer.IObserver;
import com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.http.FileTransferUtil;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTMetaPkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.HdfsFetchPkg;
import com.jhcomn.lambda.framework.lambda.pkg.cache.tt.HdfsFetchTTPkg;
import com.jhcomn.lambda.framework.lambda.speed.persistence.hdfs.SpeedHdfs2ParquetWrapper;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadWriteHdfsDao;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadOnlyHdfsDaoImpl;
import com.jhcomn.lambda.framework.lambda.persistence.hdfs.impl.ReadWriteHdfsDaoImpl;
import com.jhcomn.lambda.packages.IPackage;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * HDFS观察者
 * Created by shimn on 2016/12/26.
 */
public class HdfsObserver implements IObserver {

    private static Logger log = LoggerFactory.getLogger(HdfsObserver.class);

    private ReadWriteHdfsDao readWriteDao = null;
//    private FileSystem fileSystem = null;

    public HdfsObserver(FileSystem fileSystem) {
//        this.fileSystem = fileSystem;
        readWriteDao = new ReadWriteHdfsDaoImpl(fileSystem, new ReadOnlyHdfsDaoImpl(fileSystem));
        //检验batch层数据目录是否创建
        validate(Properties.ROOT_DATA_PATH);
    }

    /**
     * 检验目录是否创建
     */
    private void validate(String path) {
        if (readWriteDao != null) {
            if (!readWriteDao.exists(path)) {
                readWriteDao.mkdirs(path);
            }
        }
    }

    @Override
    public void update(IPackage pkg) {
        //读取hbase更新数据，从远端server拷贝数据到hdfs，并生成parquet
        HdfsFetchTTPkg fetchPkg = (HdfsFetchTTPkg) pkg;
        System.out.println("update pkg : " + fetchPkg.toString());

        //Step 1 --> upload local file to HDFS
        if (fetchPkg != null) {
            //TODO 频繁开辟新线程，需要优化
            new Thread(() -> {
                try {
                    uploadToHdfs(fetchPkg);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }).start();
        }
        else {
            log.info("HdfsObserver update wrong failed : HdfsFetchPackage is null");
        }
    }

    /**
     * 上传数据到HDFS
     * @param fetchPkg
     */
    private void uploadToHdfs(HdfsFetchTTPkg fetchPkg) {
        String key = fetchPkg.getKey();
        //TODO 根据key不同拉取数据到不同的目录文件夹下
        if (key.equals(ConstantDatas.TRAINING)) {
            //上传训练集数据
            System.out.println("上传训练集数据");
            uploadData(fetchPkg, Properties.HDFS_RAW_TRAIN_DATA_PATH, key);
        }
        else if (key.equals(ConstantDatas.TESTING)) {
            //上传测试集数据
            System.out.println("上传测试集数据");
            uploadData(fetchPkg, Properties.HDFS_RAW_TEST_DATA_PATH, key);
        }
    }

    /**
     * 上传数据
     * @param fetchPkg
     * @param subPath
     * @@param key
     */
    private void uploadData(HdfsFetchTTPkg fetchPkg, String subPath, String key) {
        String date = fetchPkg.getDate();
        List<HdfsFetchTTMetaPkg> lists = fetchPkg.getMetaPkgs();
        //实例化wrapper
        SpeedHdfs2ParquetWrapper wrapper = new SpeedHdfs2ParquetWrapper(date);
        //拷贝文件到HDFS
        lists.stream().filter(metaPkg -> metaPkg != null).forEach(metaPkg -> {
            String path = Properties.ROOT_DATA_PATH
                    + "/" + metaPkg.getUid()
                    + "/" + metaPkg.getType()
                    + subPath;
//            System.out.println("上传 path = " + path);
            validate(path);

            //拷贝文件到HDFS
            String url = metaPkg.getUrl();
            if (url == null) {
                System.out.println("metaPkg (id:" + metaPkg.getId() + ") url is null，此包不做拷贝到HDFS的处理");
            } else {
                if (url.startsWith("http")) {
                    //TODO 直接http下载流拷贝到hdfs
                    metaPkg.getFiles().stream().filter(trainingData -> trainingData != null).forEach(trainingData -> {
                        int ret = FileTransferUtil.uploadWithHttpStream(readWriteDao,
                                url + trainingData.getFile(),
                                path + "/" + trainingData.getFile());
                        if (ret == 0)
                            System.out.println("upload from " + url + trainingData.getFile() + " to hdfs 成功");
                        else if (ret == -1)
                            System.out.println("upload from " + url + trainingData.getFile() + " to hdfs 失败");
                    });
                }
                else {
                    //拷贝本地文件到HDFS
                    metaPkg.getFiles().stream().filter(trainingData -> trainingData != null).forEach(trainingData -> {
                        readWriteDao.copyFromLocalFile(new Path(url + "/" + trainingData.getFile()),
                                new Path(path + "/"));
                        System.out.println("copy from " + url + "/" + trainingData.getFile() + " to hdfs " + path);
                    });
                }
                //从HDFS中读取文件结构化到Parquet
                wrapper.setMetaPkg(metaPkg);
                wrapper.setKey(key);
                wrapper.dispatch();
            }
        });
    }
}

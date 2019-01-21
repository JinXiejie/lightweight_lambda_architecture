package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.http;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.dao.ReadWriteHdfsDao;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件传输工具类
 * Created by shimn on 2017/4/24.
 */
public class FileTransferUtil {

    public static int uploadWithHttpStream(ReadWriteHdfsDao dao, String src, String dst) {
        if (dao == null || src == null || src.equals("") || dst == null || dst.equals(""))
            return -1;
        InputStream is = HttpUtil.getStream(src);
        if (is == null)
            return -1;
        try {
            return dao.uploadWithStream(is, dst);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}

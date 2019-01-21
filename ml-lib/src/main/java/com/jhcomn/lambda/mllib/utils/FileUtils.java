package com.jhcomn.lambda.mllib.utils;

import org.apache.http.util.TextUtils;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by shimn on 2017/11/2.
 */
public class FileUtils {

    /**
     * java nio方式合并文件
     * @param paths 合并前文件路径
     * @param descPath  合并文件
     * @param append 是否为append模式修改desc目标文件
     * @param isDel 是否删除合并前的所有文件
     * @return  合并成功否
     */
    public static boolean mergeFiles(String[] paths, String descPath, boolean append, boolean isDel) {
        if (paths == null || paths.length <= 0 || descPath == null)
            return false;
        File[] files = new File[paths.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(paths[i]);
            if (paths[i] == null || !files[i].exists() || !files[i].isFile())
                return false;
        }
        File resultFile = new File(descPath);
        FileChannel channel = null;
        try {
            channel = new FileOutputStream(resultFile, append).getChannel();
            for (File f : files) {
                FileChannel blk = new FileInputStream(f).getChannel();
                channel.transferFrom(blk, channel.size(), blk.size());
                blk.close();
            }
            if (isDel) {
                for (int i = 0; i < paths.length; i++) {
                    files[i].delete();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (channel != null)
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

}

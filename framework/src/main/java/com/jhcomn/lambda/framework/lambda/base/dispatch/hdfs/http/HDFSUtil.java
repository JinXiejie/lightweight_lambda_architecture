package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.http;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

/**
 * HDFS工具类
 * https://segmentfault.com/a/1190000002610514
 */
public class HDFSUtil {
    private String hdfs_node = "";
    private String hdfs_path = "";
    private String file_path = "";
    private String hadoop_site = "";
    private String hadoop_default = "";
    private Configuration conf = null;

    public HDFSUtil(String hdfs_node) {
        this.hdfs_node = hdfs_node;
    }

    public String getHdfsNode() {
        return this.hdfs_node;
    }

    public void setHdfsPath(String hdfs_path) {
        this.hdfs_path = hdfs_path;
    }

    public String getHdfsPath() {
        return this.hdfs_path;
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }

    public String getFilePath() {
        return this.file_path;
    }

    public void setHadoopSite(String hadoop_site) {
        this.hadoop_site = hadoop_site;
    }

    public String getHadoopSite() {
        return this.hadoop_site;
    }

    public void setHadoopDefault(String hadoop_default) {
        this.hadoop_default = hadoop_default;
    }

    public String getHadoopDefault() {
        return this.hadoop_default;
    }

    public int setConfigure(boolean flag) {
        if (flag == false) {
            if (this.getHadoopSite() == "" || this.getHadoopDefault() == "") {
                return -1;
            } else {
                this.conf = new Configuration(false);
                conf.addResource(this.getHadoopDefault());
                conf.addResource(this.getHadoopSite());
                conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
                conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
                return 0;
            }
        }
        this.conf = new Configuration();
        return 0;
    }

    public Configuration getConfigure() {
        return this.conf;
    }

    public int upLoad(String localName, String remoteName) throws FileNotFoundException, IOException {
        InputStream inStream = null;
        FileSystem fs = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(localName));
            fs = FileSystem.get(URI.create(this.hdfs_node), this.conf);
            OutputStream outStream = fs.create(new Path(remoteName), new Progressable() {
                public void progress() {
                    System.out.print('.');
                }
            });

            IOUtils.copyBytes(inStream, outStream, 4096, true);
            inStream.close();
            return 0;
        } catch (IOException e) {
            inStream.close();
            e.printStackTrace();
            return -1;
        }
    }

    public int upLoad(InputStream inStream, String remoteName) throws FileNotFoundException, IOException {
        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(this.hdfs_node), this.conf);
            OutputStream outStream = fs.create(new Path(remoteName), new Progressable() {
                public void progress() {
                    System.out.print('.');
                }
            });

            IOUtils.copyBytes(inStream, outStream, 4096, true);
            inStream.close();
            return 0;
        } catch (IOException e) {
            inStream.close();
            e.printStackTrace();
            return -1;
        }
    }

    public int donwLoad(String remoteName, String localName, int lines) throws FileNotFoundException, IOException {
        FileOutputStream fos = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = null;
        OutputStreamWriter osw = null;
        BufferedWriter buffw = null;
        PrintWriter pw = null;
        FileSystem fs = null;
        InputStream inStream = null;
        try {
            fs = FileSystem.get(URI.create(this.hdfs_node + remoteName), this.conf);
            inStream = fs.open(new Path(this.hdfs_node + remoteName));
            fos = new FileOutputStream(localName);
            osw = new OutputStreamWriter(fos, "UTF-8");
            buffw = new BufferedWriter(osw);
            pw = new PrintWriter(buffw);
            isr = new InputStreamReader(inStream, "UTF-8");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null && lines > 0) {
                lines--;
                pw.println(str);
            }
        } catch (IOException e) {
            throw new IOException("Couldn't write.", e);
        } finally {
            pw.close();
            buffw.close();
            osw.close();
            fos.close();
            inStream.close();
        }
        return 0;
    }

    //main to test
    public static void main(String[] args) {
        String hdfspath = null;
        String localname = null;
        String hdfsnode = null;
        int lines = 0;

        if (args.length == 4) {
            hdfsnode = args[0];
            hdfspath = args[1];
            localname = args[2];
            lines = Integer.parseInt(args[3]);
        } else {
            hdfsnode = "hdfs://nj01-nanling-hdfs.dmop.baidu.com:54310";
            hdfspath = "/app/ps/spider/wdmqa/wangweilong/test/HDFSUtil.java";
            localname = "/home/work/workspace/project/dhc2-0/dhc/base/ftp/papapa";
            lines = 5;
        }
        HDFSUtil hdfsutil = new HDFSUtil(hdfsnode);
        hdfsutil.setFilePath(hdfsutil.getHdfsNode() + hdfspath);
        hdfsutil.setHadoopSite("./hadoop-site.xml");
        hdfsutil.setHadoopDefault("./hadoop-default.xml");
        hdfsutil.setConfigure(false);
        try {
            hdfsutil.donwLoad(hdfspath, localname, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
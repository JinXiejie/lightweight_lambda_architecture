package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.http;

import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * http下载器v1.0
 * @author shimn
 * https://segmentfault.com/a/1190000002610529
 */
public class HttpUtil{

    public static InputStream getStream(String url){
        InputStream inStream = null;
        try{
            URL httpurl = new URL(url);
            URLConnection conn = httpurl.openConnection();
            inStream = conn.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return inStream;
    }

    public static int downLoad(String url,String localName ,int lines) throws FileNotFoundException, IOException{
        FileOutputStream fos = null;
        InputStream inStream = null;
        int ret = 0;
        try{
            URL httpurl = new URL(url);
            URLConnection conn = httpurl.openConnection();
            inStream = conn.getInputStream();
            fos = new FileOutputStream(localName);
            byte[] b = new byte[102400];
            int j = 0;
            while(inStream.read(b) != -1 && lines > 0){
                for(int i = j; i < b.length; i++){
                    if(b[i] == '\n'){
                        fos.write(b, j, i - j + 1);
                        lines--;
                        if(lines <= 0){
                            break;
                        }
                        j = i + 1;
                        continue;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ret = -1;
        }finally {
            fos.close();
            inStream.close();
            return ret;
        }
    }

}
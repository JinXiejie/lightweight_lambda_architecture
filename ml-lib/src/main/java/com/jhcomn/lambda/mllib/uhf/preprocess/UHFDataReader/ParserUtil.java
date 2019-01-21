package com.jhcomn.lambda.mllib.uhf.preprocess.UHFDataReader;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class ParserUtil {
    /**
     * 从流中读取一个字符串
     * @param dis
     * @param byteCount 字节数
     * @return
     * @throws IOException
     */
    public static String readString(DataInputStream stream , int byteCount) throws IOException{
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<byteCount; i++){
            sb.append((char)stream.readByte());
        }
        return sb.toString().trim();
    }

    public static String readString(SwappedDataInputStream stream , int byteCount) throws IOException{
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<byteCount; i++){
            sb.append((char)stream.readByte());
        }
        return sb.toString().trim();
    }
}

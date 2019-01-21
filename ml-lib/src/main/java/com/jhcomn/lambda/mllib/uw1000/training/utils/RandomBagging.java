package com.jhcomn.lambda.mllib.uw1000.training.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 有放回的随机抽样
 * Created by shimn on 2017/11/2.
 */
public class RandomBagging {

    private List<String> cache = null;

    public RandomBagging() {
        cache = new ArrayList<String>();
    }

    /**
     * 有放回随机采样N份数据
     * @param src
     * @param desc
     * @param N
     * @return
     * @throws IOException
     */
    public boolean baggingLocal (String src, String desc, int N) {
        BufferedWriter bw = null;
        try {
            int totals = getTotalLines(new File(src), true);
            bw = new BufferedWriter(new FileWriter(new File(desc)));
            Random random = new Random();
            for (int i = 0; i < N; i++) {
                int index = random.nextInt(totals - 1); //最后一行为空，所以totals-1，防止空指针错误
                String line = cache.get(index);
                if (line == null)
                    System.out.println("index = " + index + ", cache's size = " + cache.size());
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return true;
    }

    /**
     * 获取文件的行数
     * @param file
     * @param isCache
     * @return
     * @throws IOException
     */
    public int getTotalLines(File file, boolean isCache) throws IOException {
        if (isCache) {
            cache = null;
            cache = new ArrayList<String>();
        }

        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String line = reader.readLine();
        int lines= 0;
        while (line != null) {
            lines++;
            line = reader.readLine();
            if (isCache)
                cache.add(line);
        }
        reader.close();
        in.close();
        return lines;
    }

    public static void main(String[] args) {
//        String src = "D:\\jhcomnBigData\\uw_classify\\source\\original\\normal.data";
//        String desc = "D:\\jhcomnBigData\\uw_classify\\source\\original\\normal_bagging1.data";
//        RandomBagging bagging = new RandomBagging();
//        bagging.baggingLocal(src, desc, 50);
    }
}

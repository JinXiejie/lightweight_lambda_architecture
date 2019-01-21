package com.jhcomn.lambda.mllib.base.serializer;

import java.io.*;

/**
 * java本地序列化
 * Created by shimn on 2017/11/13.
 */
public class LocalSerializer implements ISerializer {
    @Override
    public <T> void serialize(T obj, String path) {
        if (obj == null || path == null || path.equalsIgnoreCase("")) {
            System.out.println("null exception about serializing in LocalSerializer.");
            return;
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> T deserialize(String path) {
        if (path == null || path.equalsIgnoreCase("")) {
            System.out.println("null exception about deserializing in LocalSerializer.");
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

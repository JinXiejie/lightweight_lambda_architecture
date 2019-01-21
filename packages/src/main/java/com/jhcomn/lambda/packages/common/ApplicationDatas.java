package com.jhcomn.lambda.packages.common;

import com.jhcomn.lambda.packages.tag.TagListCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shimn on 2017/5/22.
 */
public class ApplicationDatas {

    //标签列表缓存map --> key为试验类型
    public static Map<String, TagListCache> tagListCacheMap = new HashMap<>();

    public static final String UW = "UW";
    public static final String TEV = "TEV";
    public static final String INFRARED = "INFRARED";
    public static final String HFCT = "HFCT";
    public static final String TEST = "TEST";
    public static final String HELLO_WORLD = "HELLO_WORLD";
    public static final String UHF = "UHF";
}

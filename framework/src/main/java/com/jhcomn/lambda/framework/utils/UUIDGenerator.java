package com.jhcomn.lambda.framework.utils;

import java.util.UUID;

/**
 * UUID生成器
 * @author becky
 */
public class UUIDGenerator {

	public UUIDGenerator() {
	}

	//获取单个UUID
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		return id;
	}
	
	//获取一定数量UUID
	public static String[] getUUID(int number) {
		if(number < 1)
			return null;
		
		String[] id = new String[number];
		for(int i = 0; i < number; i++) {
			id[i] = getUUID();
		}
		return id;
	}
}

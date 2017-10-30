package com.mrathena.wechat.publicity.common.tool;

public class Kit {
	
	private Kit() {}

	public static void main(String[] args) {
		String a = "";
		System.out.println(areNull(null, null));
		System.out.println(areNotNull(a, a));
	}

	/** 所有参数全为null, 返回true, 只要有一个不为null, 返回false */
	public static boolean areNull(Object... objects) {
		for (Object object : objects) {
			if (object != null) {
				return false;
			}
		}
		return true;
	}

	/** 所有参数全不为null, 返回true, 只要有一个为null, 返回false */
	public static boolean areNotNull(Object... objects) {
		for (Object object : objects) {
			if (object == null) {
				return false;
			}
		}
		return true;
	}
}

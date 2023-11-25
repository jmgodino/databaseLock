package com.picoto;

public class Utils {

	public static void debug(String string) {
		System.out.println(Thread.currentThread().getName()+"-"+System.currentTimeMillis()+"-"+string);
	}

}

package org.wt.example2;

public class TypeUtils {
	public static String slashIfy(String className){
		if(className==null){
			return null;
		}
		return className.replaceAll("\\.", "/");
	}
	public static String dotIfy(String className){
		if(className==null){
			return null;
		}
		return className.replaceAll("/", ".");
	}
}

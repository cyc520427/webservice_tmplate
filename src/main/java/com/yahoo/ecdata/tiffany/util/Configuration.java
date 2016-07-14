package com.yahoo.ecdata.tiffany.util;

import java.util.Properties;

@SuppressWarnings("serial")
public class Configuration extends Properties{
	
	private ClassLoader classLoader=Configuration.class.getClassLoader();
	
	public Configuration(){
    	try {
			this.load(classLoader.getResourceAsStream("config-default.properties"));
			this.load(classLoader.getResourceAsStream("config.properties"));
		} catch (Throwable e) {
			throw new RuntimeException("Unable to load configuration file from classpath",e);
		}
	}
}

package org.ekstep.common.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ReadProperties {

	private static Properties prop = new Properties();
	private static InputStream input = null;

	static {
		loadProperties("content.properties");
	}
	
	/**
	 * @params key to get Property
     * @return the property
     */
	public static String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	public static void loadProperties(Map<String, Object> props) {
		prop.putAll(props);
	}
	
	public static void loadProperties(Properties props) {
		prop.putAll(props);
	}
	
	/**
	 * @params File from where to load the properties
	 * loads all properties from the file
	 * throws exception if the file is empty/absent
     */
	public static void loadProperties(String filename) {
		try {
			input = ReadProperties.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				throw new Exception("Unable to find " + filename);
			}
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

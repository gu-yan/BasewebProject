package cn.gy.test.configs.fileutil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.gy.test.configs.constants.ConstantBasic;

/**
 * load multiple property files <br>
 * Files relative path defined at {@link cn.gy.test.configs.constants.ConstantBasic.CONFIG_FILE} <br>
 * Read files by prefix path {@link cn.gy.test.configs.constants.ConstantBasic.userDir} + relative path
 * @author guyan
 * 2016年3月20日
 */
public class ConfigFileUtils {

	public static final Map<String, Properties> propertiesMap = new HashMap<>();

	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public ConfigFileUtils() {

	}

	static {
		try {
			String[] files = ConstantBasic.CONFIG_FILE;
			init(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized public static void init(String... files) {
		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].trim();
			if (filePath.length() > 0) {
				Properties properties = IOUtils.
						getPropertiesFromFile(ConstantBasic.userDir + File.separator + filePath,DEFAULT_ENCODING);
				if (null != properties) {
					propertiesMap.put(filePath, properties);
				}
			}
		}
	}
	
	public static String getPropertyValue(String file, String name) {
		Properties properties = propertiesMap.get(file);
		if (null != properties) {
			return properties.getProperty(name);
		}
		return null;
	}
}

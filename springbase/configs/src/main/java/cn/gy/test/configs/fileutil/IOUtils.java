package cn.gy.test.configs.fileutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class IOUtils {

	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static Properties getPropertiesFromFile(String filePath, String encoding) { 
		Properties properties = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			properties = new Properties();
			
			fis = new FileInputStream(filePath);
			if (StringUtils.isBlank(encoding)) {
				isr = new InputStreamReader(fis, DEFAULT_ENCODING);
			} else {
				isr = new InputStreamReader(fis, encoding);
			}
			br = new BufferedReader(isr);
			
			properties.load(br);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return properties;
	}
}

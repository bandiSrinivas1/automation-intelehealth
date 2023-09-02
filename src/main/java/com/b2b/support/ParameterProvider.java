package com.b2b.support;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Harish
 *
 */

public class ParameterProvider {
	protected static final Logger logger = LogManager.getLogger(B2BBy.class);
	
	public static final String LANG_RESOURCES_FILE_PATH = "src/main/resources/lang/";
	private static  ParameterProvider instance = null;
	private static Map<String, Properties> CONFIG_FILES_KEY = new ConcurrentHashMap<String, Properties>();	
	
	protected String getMessage(String fileName, String key) {
		return CONFIG_FILES_KEY.get(fileName).getProperty(key, null);
	}
	
	
	protected Map<String, Properties> getTestDataPropertiesMap() {
		return CONFIG_FILES_KEY;
	}
	
	public static ParameterProvider getInstance() {
		
		if (instance == null) {
			instance = new ParameterProvider();
		}
		return instance;
	}
	/**
	 * load the xpath file
	 * @param lang
	 */
	public void loadPropertiesFiles(String lang) {
		if (Optional.ofNullable(lang).isPresent()) {
			lang = "en";
		}
		
		try {
			 List<File> files = Files.list(Paths.get(LANG_RESOURCES_FILE_PATH+lang.toLowerCase()))
					    .map(Path::toFile)
					    .filter(File::isFile)
					    .collect(Collectors.toList());
			
			for (File file : files) {
				try(FileReader applicationFileReader = new FileReader(file)) {
					Properties p = new Properties();
					p.load(applicationFileReader);
					CONFIG_FILES_KEY.put(file.getName().split("_")[0], p);
				} catch (IOException e) {
					logger.error("Exception occur while loading the config file", e);
				} 
			}
		} catch (IOException e) {
			logger.error("Exception occur while loading the config file", e);
		}
	}

	public String getParameter(String fileName, String property) {
		System.out.println("file nAME*******************************************"+fileName);
		System.out.println("property nAME*************************************"+property);
		return getMessage(fileName, property);
		
	}
	
}
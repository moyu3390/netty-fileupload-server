package cn.com.moyu3390.core.fileservice.log;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class LogConfigLoader {

	public static void init(String filePath) {
		Logger logger = null;
		// 指定log4j2.xml文件的位置
		ConfigurationSource source;
		File log4jFile = new File(filePath);
		try {
			if (log4jFile.exists()) {
				source = new ConfigurationSource(new FileInputStream(log4jFile), log4jFile);
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				Configurator.initialize(classLoader, source);
			}
			logger = LogManager.getLogger(LogConfigLoader.class);
			if (log4jFile.exists()) logger.info("日志配置加载成功,加载配置文件路径：" + filePath);
			else logger.info("日志配置加载成功,加载默认配置文件：log4j2.xml");
		} catch (Exception e) {
			logger.info("日志配置加载失败,请检查log4j2.xml是否存在", e);
		}

	}

	private LogConfigLoader() {}
}

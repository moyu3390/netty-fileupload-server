package cn.com.moyu3390.core.fileservice.log;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.util.StatusPrinter;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

public class LogConfigLoader {
    private static Logger log = LoggerFactory.getLogger(LogConfigLoader.class);


    // log4j2配置
    public static void initLog4j(String filePath) {
        org.apache.logging.log4j.Logger logger = null;
        // 指定log4j2.xml文件的位置
        ConfigurationSource source;
        File log4jFile = new File(filePath);
        try {
            if (log4jFile.exists()) {
                source = new ConfigurationSource(new FileInputStream(log4jFile), log4jFile);
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                org.apache.logging.log4j.core.config.Configurator.initialize(classLoader, source);
            }
            logger = org.apache.logging.log4j.LogManager.getLogger(LogConfigLoader.class);
            if (log4jFile.exists()) {
                logger.info("日志配置加载成功,加载配置文件路径：" + filePath);
            } else {
                logger.info("日志配置加载成功,加载默认配置文件：log4j2.xml");
            }
        } catch (Exception e) {
            logger.info("日志配置加载失败,请检查log4j2.xml是否存在", e);
        }

    }

    ///**
    // * 初始化logback配置文件
    // *
    // * @param logFilePath
    // */
    //public static void initLogBack(String logFilePath) {
    //    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    //    JoranConfigurator joranConfigurator = new JoranConfigurator();
    //    joranConfigurator.setContext(loggerContext);
    //    loggerContext.reset();
    //    try {
    //        joranConfigurator.doConfigure(new File(logFilePath));
    //    } catch (Exception e) {
    //        log.error("Load logback config file error. Message: " + e.getMessage(), e);
    //    }
    //    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    //}

    private LogConfigLoader() {
    }
}

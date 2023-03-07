/**
 * JobLog4jFactory
 * <p>
 * 1.0
 * <p>
 * 2023/3/3 13:53
 */

package cn.com.moyu3390.core.fileservice.log.factory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @描述
 * @创建人 HeChenglong
 * @创建时间 2023/3/3
 */
public class JobLog4jFactory {
    private JobLog4jFactory() {
    }

    public static void start(int jobId) {
        //为false时，返回多个LoggerContext对象，   true：返回唯一的单例LoggerContext
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        //创建一个展示的样式：PatternLayout，   还有其他的日志打印样式。
        Layout layout = PatternLayout.newBuilder()
                .withCharset(Charset.forName("UTF-8"))
                .withConfiguration(config)
                .withPattern("%d %t %p %X{TracingMsg} %c - %m%n")
                .build();
        //final TriggeringPolicy policy = TimeBasedTriggeringPolicy.newBuilder()
        //        .withModulate(true)
        //        .withInterval(1)
        //        .build();
        TriggeringPolicy tp = SizeBasedTriggeringPolicy.createPolicy("10MB");

        //  日志打印方式——输出为文件
        final Appender appender = RollingFileAppender.newBuilder()
                .setName(jobId + "DomainCntCoreLog")
                .withImmediateFlush(true)
                .withFileName("logs/" + jobId + "/CNTCore.log")
                .withFilePattern("logs/" + jobId + "/CNTCore.log.%d{yyyy-MM-dd-a}.gz")
                .setLayout(layout)
                .withPolicy(tp)
                .build();


        //Appender appender = FileAppender.createAppender(
        //        String.format("logs/test/syncshows-job-%s.log", jobId), "true", "false",
        //        "" + jobId, null, "true", "true", null, layout, null, null, null, config);
        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("" + jobId, null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL, "" + jobId,
                "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("" + jobId, loggerConfig);
        ctx.updateLoggers();
    }

    public static void stop(int jobId) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        config.getAppender(jobId + "DomainCntCoreLog").stop();
        config.getLoggerConfig("" + jobId).removeAppender(jobId + "DomainCntCoreLog");
        config.removeLogger("" + jobId);
        ctx.updateLoggers();
    }

    /**
     * 获取Logger
     * <p>
     * 如果不想使用slf4j,那这里改成直接返回Log4j的Logger即可
     *
     * @param jobId
     * @return
     */
    public static Logger createLogger(int jobId) {
        start(jobId);

        return LoggerFactory.getLogger("" + jobId);
    }
}

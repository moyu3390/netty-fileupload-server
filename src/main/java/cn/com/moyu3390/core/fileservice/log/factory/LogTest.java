/**
 * LogTest
 * <p>
 * 1.0
 * <p>
 * 2023/3/3 14:31
 */

package cn.com.moyu3390.core.fileservice.log.factory;

import org.slf4j.Logger;

/**
 *
 * @描述
 *
 * @创建人 HeChenglong
 * @创建时间 2023/3/3
 */
public class LogTest {
    public static void main(String[] args) {
        for(int i=0;i<5;i++) {
            Logger logger = JobLog4jFactory.createLogger(i);
            logger.info("Testing testing testing 111");
            logger.debug("Testing testing testing 222");
            logger.error("Testing testing testing 333");
            JobLog4jFactory.stop(i);
        }
    }
}

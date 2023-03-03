package cn.com.moyu3390.core.fileservice.client.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.moyu3390.core.fileservice.client.FileUploadClient;
import cn.com.moyu3390.core.fileservice.client.sender.ClientSenderThread;
import cn.com.moyu3390.core.fileservice.log.LogConfigLoader;

/**
 * 客户端测试类
 * 
 * @author admin
 *
 */
public class FileUploadTest {
	// 获取服务器类型
	// 获取日志服务器列表
	// 根据类型组装日志收集器
	// 调用线程池发送至多个服务器

	public static void main(String[] args) {
		// 如果是自定义配置log4j2.xml路径，在项目启动前需要加载配置，如果log4j2.xml放在了src/main/resources目录下，则不需要
		String fileName = "config/log/log4j2.xml";
		LogConfigLoader.initLog4j(fileName);
		
		String file2 = "F:\\cn_project_professional_2013_with_sp1_x86_and_x64_dvd_3911523.iso";
		String file3 = "F:\\jdk-7u80-linux-i586.tar.gz";
		String file4 = "F:\\Kylin-4.0.2-server-sp2-17122515.Z1-x86_64.iso";
		String file5 = "F:\\MemoryAnalyzer-1.7.0.20170613-win32.win32.x86_64.zip";

		ExecutorService executor = Executors.newFixedThreadPool(3);
		// 启动客户端
		FileUploadClient client = new FileUploadClient("172.16.7.61", 8127, 3);
		// executor.execute(new ClientSenderThread(file3, client));
		// executor.execute(new ClientSenderThread(file4, client));
		// executor.execute(new ClientSenderThread(file5, client));

		FileUploadClient client2 = new FileUploadClient("127.0.0.1", 8127, 1);
		// executor.execute(new ClientSenderThread(file4, client2));

		List<FileUploadClient> clients = new ArrayList<FileUploadClient>();
		clients.add(client);
		executor.execute(new ClientSenderThread(file2, client, "business_log"));
		executor.execute(new ClientSenderThread(file4, client, "business_log"));
		executor.execute(new ClientSenderThread(file5, client, "business_log"));
		clients.add(client2);
		executor.execute(new ClientSenderThread(file3, client2, "business_log"));
		for (int i = 0; i < clients.size(); i++) {
			FileUploadClient cli = clients.get(i);
			try {
				cli.latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("关闭客户端：" + cli.hashCode());
				cli.shutDown();
			}
		}

		executor.shutdown();

		// try {
		// client.latch.await();
		// client2.latch.await();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } finally {
		// client.shutDown();
		// client2.shutDown();
		// }

		System.out.println("执行完毕");

	}
}

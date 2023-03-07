package cn.com.moyu3390.core.fileservice.server.example;

import cn.com.moyu3390.core.fileservice.log.LogConfigLoader;
import cn.com.moyu3390.core.fileservice.server.FileUploadServer;

public class ServerStart {
	private static int		prot	= 8127;
	private static String	host	= "0.0.0.0";

	public static void main(String[] args) {
		// 如果是自定义配置 logback.xml路径，在项目启动前需要加载配置，如果 logback.xml放在了src/main/resources目录下，则不需要
		String fileName = "config/log/logback.xml";
		//LogConfigLoader.initLogBack(fileName);
		FileUploadServer server = new FileUploadServer(host, prot);
		server.start();
	}
}

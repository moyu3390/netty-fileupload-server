package cn.com.moyu3390.core.fileservice.client.sender;

import java.io.File;

import cn.com.moyu3390.core.fileservice.client.FileUploadClient;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;
import cn.com.moyu3390.core.fileservice.utils.MsgUtil;
import io.netty.channel.ChannelFuture;

public class ClientSenderThread implements Runnable {

	private String				filePath;
	private FileUploadClient	client;
	private String				fileType;

	public ClientSenderThread(String path, FileUploadClient cli, String fileType) {
		this.filePath = path;
		this.client = cli;
		this.fileType = fileType;
	}

	@Override
	public void run() {
		try {
			ChannelFuture channelFuture = client.connect();
			// 文件信息{文件大于1024kb方便测试断点续传}
			File file = new File(filePath);
			FileTransferProtocol fileTransferProtocol = MsgUtil.buildRequestTransferFile(file.getAbsolutePath(),
					file.getName(), fileType, file.length());
			// 发送信息；请求传输文件
			channelFuture.channel().writeAndFlush(fileTransferProtocol);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

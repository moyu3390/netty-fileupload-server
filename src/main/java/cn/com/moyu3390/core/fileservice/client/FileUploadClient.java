package cn.com.moyu3390.core.fileservice.client;

import cn.com.moyu3390.core.fileservice.server.FileUploadServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FileUploadClient {
	private static Logger logger	= LoggerFactory.getLogger(FileUploadServer.class);
	private String				serverIp;
	private int					serverPort;
	// 配置客户端NIO线程组
	private EventLoopGroup		workerGroup		= new NioEventLoopGroup();
	private Bootstrap			b				= new Bootstrap();
	// 客户端TCP连接集合
	private List<ChannelFuture>	channelFutures	= new ArrayList<ChannelFuture>();
	public CountDownLatch		latch;

	public FileUploadClient(String ip, int port, int latchCount) {
		this.serverIp = ip;
		this.serverPort = port;
		latch = new CountDownLatch(latchCount);
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.AUTO_READ, true);
		b.handler(new FileUploadClientChannelInitializer());
	}

	public ChannelFuture connect() throws Exception {
		ChannelFuture channelFuture = null;
		try {
			channelFuture = b.connect(serverIp, serverPort).sync();
			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					latch.countDown();
				}
			});
			// 去除jdk8的lambda 语法，兼容jdk8以下版本
//			channelFuture.channel().closeFuture().addListener((x) -> {
//				latch.countDown();
//			});
			channelFutures.add(channelFuture);
			return channelFuture;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != channelFuture && channelFuture.isSuccess()) {
				logger.info("客户端连接成功");
			} else {
				logger.info("客户端连接失败");
			}
		}

	}

	public void shutDown() {
		workerGroup.shutdownGracefully();
	}
}

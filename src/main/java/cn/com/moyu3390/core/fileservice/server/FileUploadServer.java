package cn.com.moyu3390.core.fileservice.server;


import cn.com.moyu3390.core.fileservice.exception.FileServiceException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 文件上传服务端 (单独启动使用此类，不可去除)
 * 
 * @author admin
 *
 */
public class FileUploadServer {
	private static Logger logger	= LoggerFactory.getLogger(FileUploadServer.class);
	private int				port;
	private String			bindHost;

	public FileUploadServer(String bindHost, int pt) {
		this.port = pt;
		this.bindHost = bindHost;
	}

	public int getServerPort() {
		return port;
	}

	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(getThreadNumber());
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true).localAddress(bindHost, port)
					.childHandler(new FileUploadServerChannelInitializer());
			ChannelFuture f = server.bind().sync();
			logger.info("文件服务启动成功");
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("Start netty with port:" + port + " failed.", e);
			throw new FileServiceException("FF1C0401", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private static int getThreadNumber() {
		double coefficient = 0.8;
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		int poolSize = (int) (numberOfCores / (1 - coefficient));
		return poolSize;
	}

}

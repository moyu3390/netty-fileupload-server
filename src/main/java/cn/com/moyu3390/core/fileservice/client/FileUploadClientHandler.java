package cn.com.moyu3390.core.fileservice.client;

import cn.com.moyu3390.core.fileservice.protocol.Constants;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstData;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstInstruct;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;
import cn.com.moyu3390.core.fileservice.server.FileUploadServer;
import cn.com.moyu3390.core.fileservice.utils.FileUtil;
import cn.com.moyu3390.core.fileservice.utils.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端上传文件handler
 * 
 * @author admin
 *
 */
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger	= LoggerFactory.getLogger(FileUploadServer.class);

	/**
	 * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		SocketChannel channel = (SocketChannel) ctx.channel();
		logger.info("链接报告信息：客户端链接到服务端。channelId：" + channel.hashCode());
		logger.info("链接报告IP:" + channel.localAddress().getHostString());
		logger.info("链接报告Port:" + channel.localAddress().getPort());
	}

	/**
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("断开链接" + ctx.channel().localAddress().toString() + ";channleid:" + ctx.channel().hashCode());
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 数据格式验证
		if (!(msg instanceof FileTransferProtocol)){
			return;
		}

		FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
		// 0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
		switch (fileTransferProtocol.getTransferType()) {
		case 1:
			FileBurstInstruct fileBurstInstruct = (FileBurstInstruct) fileTransferProtocol.getTransferObj();
			// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
			if (Constants.FileStatus.COMPLETE == fileBurstInstruct.getStatus()) {
				ctx.flush();
				ctx.close();
				return;
			}
			FileBurstData fileBurstData = FileUtil.readFile(fileBurstInstruct.getClientFileUrl(),
					fileBurstInstruct.getReadPosition());
			ctx.writeAndFlush(MsgUtil.buildTransferData(fileBurstData));
			break;
		default:
			break;
		}

		/**
		 * 模拟传输过程中断，场景测试可以注释掉
		 *
		 * 
		 * ctx.flush(); ctx.close(); System.exit(-1);
		 */
	}

	/**
	 * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		logger.error("上传文件出现异常：\r\n", cause);
	}

}

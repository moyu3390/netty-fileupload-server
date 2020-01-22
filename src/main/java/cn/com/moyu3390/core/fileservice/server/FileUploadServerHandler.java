package cn.com.moyu3390.core.fileservice.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;

import cn.com.moyu3390.core.fileservice.protocol.Constants;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstData;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstInstruct;
import cn.com.moyu3390.core.fileservice.protocol.FileDescInfo;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;
import cn.com.moyu3390.core.fileservice.utils.CacheUtil;
import cn.com.moyu3390.core.fileservice.utils.FileUtil;
import cn.com.moyu3390.core.fileservice.utils.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * 服务端上传文件Handler
 * 
 * @author admin
 *
 */
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {
	private static Logger	logger		= LogManager.getLogger(FileUploadServerHandler.class);
	private String			fileDir;
	private String			fileType	= "business_type";
	private String			BaseDir		= UploadFileSavePath.save_path_temp_map.get(fileType);

	/**
	 * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		SocketChannel channel = (SocketChannel) ctx.channel();
		logger.info("链接报告信息：有一个客户端链接到本服务端。channelId：" + channel.hashCode());
		logger.info("链接报告IP:" + channel.remoteAddress().getHostString());
		logger.info("链接报告Port:" + channel.remoteAddress().getPort());
		fileDir = channel.remoteAddress().getHostString();
	}

	/**
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("客户端断开链接" + ctx.channel().localAddress().toString() + ";channelId:" + ctx.channel().hashCode());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 数据格式验证
		if (!(msg instanceof FileTransferProtocol)) return;

		FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
		// 0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
		switch (fileTransferProtocol.getTransferType()) {
		case 0:
			FileDescInfo fileDescInfo = (FileDescInfo) fileTransferProtocol.getTransferObj();
			fileType = fileDescInfo.getFileType();
			BaseDir = UploadFileSavePath.getSaveTempPath(fileType, fileDir);

			logger.info("开始上传，文件名称：" + fileDescInfo.getFileName() + "；存储路径：" + BaseDir);
			// 断点续传信息，实际应用中需要将断点续传信息保存到数据库中
			FileBurstInstruct fileBurstInstructOld = CacheUtil.burstDataMap
					.get(fileDir + "_" + fileDescInfo.getFileName());
			if (null != fileBurstInstructOld) {
				if (fileBurstInstructOld.getStatus() == Constants.FileStatus.COMPLETE) {
					CacheUtil.burstDataMap.remove(fileDir + "_" + fileDescInfo.getFileName());
				}
				// 传输完成删除断点信息
				logger.info("传输完成删除断点信息" + JSON.toJSONString(fileBurstInstructOld));
				ctx.writeAndFlush(MsgUtil.buildTransferInstruct(fileBurstInstructOld));
				return;
			}

			// 发送信息
			FileTransferProtocol sendFileTransferProtocol = MsgUtil.buildTransferInstruct(Constants.FileStatus.BEGIN,
					fileDescInfo.getFileUrl(), 0l);
			ctx.writeAndFlush(sendFileTransferProtocol);
			break;
		case 2:
			FileBurstData fileBurstData = (FileBurstData) fileTransferProtocol.getTransferObj();
			FileUtil.createDir(BaseDir);
			// logger.info("存储路径：" + BaseDir);
			FileBurstInstruct fileBurstInstruct = FileUtil.writeFile(BaseDir, fileBurstData);

			// 保存断点续传信息
			CacheUtil.burstDataMap.put(fileDir + "_" + fileBurstData.getFileName(), fileBurstInstruct);

			ctx.writeAndFlush(MsgUtil.buildTransferInstruct(fileBurstInstruct));

			// 传输完成删除断点信息
			if (fileBurstInstruct.getStatus() == Constants.FileStatus.COMPLETE) {
//				String uploadFilePath = BaseDir + File.separator + fileBurstData.getFileName();
				Path uploadFilePath = Paths.get(BaseDir + File.separator + fileBurstData.getFileName());
				logger.info("上传完成，文件名：" + uploadFilePath);

				CacheUtil.burstDataMap.remove(fileDir + "_" + fileBurstData.getFileName());
				// 把日志文件从临时目录中移走
				String savePath = UploadFileSavePath.getSavePath(fileType, fileDir);
				File saveDir = new File(savePath);
				if (!saveDir.exists()) saveDir.mkdirs();

//				String saveUploadFilePath = savePath + File.separator + fileBurstData.getFileName();
				Path saveUploadFilePath = Paths.get(savePath + File.separator + fileBurstData.getFileName());
				logger.info("移动文件从 " + uploadFilePath + " 到：" + saveUploadFilePath);

				// 移动文件，若目标文件已存在，则覆盖
				Files.move(uploadFilePath, saveUploadFilePath, StandardCopyOption.REPLACE_EXISTING);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		logger.info("接收文件出现异常：\r\n", cause);
	}

}

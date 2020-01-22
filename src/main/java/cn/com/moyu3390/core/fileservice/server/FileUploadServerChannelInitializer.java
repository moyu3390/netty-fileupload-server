package cn.com.moyu3390.core.fileservice.server;

import cn.com.moyu3390.core.fileservice.codec.ObjDecoder;
import cn.com.moyu3390.core.fileservice.codec.ObjEncoder;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 文件上传服务端初始化类
 * @author admin
 *
 */
public class FileUploadServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        //对象传输处理
        channel.pipeline().addLast(new ObjDecoder(FileTransferProtocol.class));
        channel.pipeline().addLast(new ObjEncoder(FileTransferProtocol.class));
        // 在管道中添加自己的接收数据实现方法
        channel.pipeline().addLast(new FileUploadServerHandler());
    }

}

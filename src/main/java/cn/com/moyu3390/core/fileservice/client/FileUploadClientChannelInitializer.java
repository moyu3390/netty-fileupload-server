package cn.com.moyu3390.core.fileservice.client;

import cn.com.moyu3390.core.fileservice.codec.ObjDecoder;
import cn.com.moyu3390.core.fileservice.codec.ObjEncoder;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 客户端上传文件初始化类
 * @author admin
 *
 */
public class FileUploadClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //对象传输处理
        channel.pipeline().addLast(new ObjDecoder(FileTransferProtocol.class));
        channel.pipeline().addLast(new ObjEncoder(FileTransferProtocol.class));
        // 在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new FileUploadClientHandler());
    }

}

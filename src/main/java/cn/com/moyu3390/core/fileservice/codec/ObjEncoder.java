package cn.com.moyu3390.core.fileservice.codec;

import cn.com.moyu3390.core.fileservice.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * @author admin
 *
 */
public class ObjEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;

    public ObjEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out)  {
        if (genericClass.isInstance(in)) {
            byte[] data = Serialization.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}

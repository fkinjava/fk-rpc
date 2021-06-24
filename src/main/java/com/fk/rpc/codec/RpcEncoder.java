package com.fk.rpc.codec;

import com.fk.rpc.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @className: RequestEncoder
 * @description: TODO 类描述
 * @author: jurly
 * @date: 6/23/2021
 **/
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;
    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(genericClass.isInstance(msg)) {
            byte[] data = ProtostuffUtil.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}

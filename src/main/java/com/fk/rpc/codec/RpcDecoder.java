package com.fk.rpc.codec;

import com.fk.rpc.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @className: RpcDecoder
 * @description: TODO 类描述
 * @author: jurly
 * @date: 6/23/2021
 **/
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;
    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4 ) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        //如果可读长度小于dataLength，说明没传完
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object deserialize = ProtostuffUtil.deserialize(data, genericClass);
        out.add(deserialize);
    }
}

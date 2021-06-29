package com.fk.rpc.client.handler;

import com.fk.rpc.codec.RpcDecoder;
import com.fk.rpc.codec.RpcEncoder;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4));
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new RpcDecoder(RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }
}

package com.fk.rpc.server;

import com.fk.rpc.codec.RpcDecoder;
import com.fk.rpc.codec.RpcEncoder;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;
import com.fk.rpc.server.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    private Map<String, Object> refMap = new ConcurrentHashMap<>();

    public void init(int port) {
        start(port);
    }

    private void start(int port) {
        ServerBootstrap bs = new ServerBootstrap();
        bs.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcServerHandler(refMap));
                    }
                });
        ChannelFuture channelFuture = bs.bind(port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("server start success on port:{}", port);
                } else {
                    log.info("server start fail on port:{}", port);
                }
            }
        });
    }

    public void registerRef(Object ref) {
        refMap.put(ref.getClass().getName(), ref);

    }

}

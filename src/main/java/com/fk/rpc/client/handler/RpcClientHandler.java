package com.fk.rpc.client.handler;

import com.fk.rpc.client.future.RpcFuture;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel;

    private Map<String, RpcFuture> waitForResponseFutureMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        handleResponse(msg);
    }

    private void handleResponse(RpcResponse response) {
        String requestId = response.getRequestId();
        RpcFuture future = waitForResponseFutureMap.get(requestId);
        if (future != null) {
            waitForResponseFutureMap.remove(future);
            future.done(response);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
    }

    public RpcResponse sendSync(RpcRequest rpcRequest) {
        RpcFuture future = sendAsync(rpcRequest);
        try {
            return (RpcResponse) future.get();
        } catch (InterruptedException e) {
            log.info("wait for rpcResponse interrupted", e);
        }
        return null;
    }

    public RpcFuture sendAsync(RpcRequest rpcRequest) {
        RpcFuture future = new RpcFuture(rpcRequest);
        waitForResponseFutureMap.put(rpcRequest.getRequestId(), future);
        channel.writeAndFlush(rpcRequest);
        return future;
    }
}

package com.fk.rpc.server.handler;

import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;

import java.util.Map;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String, Object> handleRefMap;

    public RpcServerHandler(Map<String, Object> handleRefMap) {
        this.handleRefMap = handleRefMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse rpcResponse = handleRequest(msg);
        ctx.writeAndFlush(rpcResponse);
    }

    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        String className = request.getClassName();
        Object ref = handleRefMap.get(className);
        FastClass fastClass = FastClass.create(ref.getClass());
        try {
            Object res = fastClass.invoke(request.getMethodName(), request.getParameterTypes(), ref, request.getParams());
            rpcResponse.setResult(res);
        }catch (Exception e) {
            rpcResponse.setThrowable(e);
        }
        return rpcResponse;
    }
}

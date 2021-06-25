package com.fk.rpc.client.connect;

import com.fk.rpc.client.future.RpcFuture;
import com.fk.rpc.client.handler.RpcClientHandler;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;

/**
 * 默认连接实现
 */
public class DefaultRpcConnection implements RpcConnection {
    private RpcClientHandler rpcClientHandler;

    public DefaultRpcConnection(RpcClientHandler rpcClientHandler) {
        this.rpcClientHandler = rpcClientHandler;
    }

    @Override
    public RpcResponse sendRequestSync(RpcRequest rpcRequest) {
        return rpcClientHandler.sendSync(rpcRequest);
    }

    @Override
    public RpcFuture sendRequestAsync(RpcRequest rpcRequest) {
        return rpcClientHandler.sendAsync(rpcRequest);
    }
}

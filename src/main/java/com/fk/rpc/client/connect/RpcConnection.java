package com.fk.rpc.client.connect;

import com.fk.rpc.client.future.RpcFuture;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;

/**
 * 代理RpcClientHandler的功能，只暴露发送数据的接口
 */
public interface RpcConnection {

    /**
     * 同步发送请求
     * @param rpcRequest
     * @return
     */
    RpcResponse sendRequestSync(RpcRequest rpcRequest);

    /**
     * 异步发送请求
     * @param rpcRequest
     * @return
     */
    RpcFuture sendRequestAsync(RpcRequest rpcRequest);

}

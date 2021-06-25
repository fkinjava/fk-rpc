package com.fk.rpc.client.proxy;

import com.fk.rpc.client.connect.ConnectionPool;
import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RpcSyncProxy implements InvocationHandler {
    private ConnectionPool pool;

    private Class<?> clazz;

    public RpcSyncProxy(ConnectionPool pool, Class<?> clazz) {
        this.pool = pool;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        RpcResponse rpcResponse = pool.getConnection().sendRequestSync(request);
        return rpcResponse.getResult();
    }
}

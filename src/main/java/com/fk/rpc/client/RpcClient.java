package com.fk.rpc.client;

import com.fk.rpc.client.connect.ConnectionPool;
import com.fk.rpc.client.proxy.RpcSyncProxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClient {
    private Map<Class<?>, Object> cachedSyncProxyInstances = new ConcurrentHashMap<>();

    private ConnectionPool pool;

    public RpcClient(String providerAddresses) {
        this.pool = new ConnectionPool();
        pool.init(providerAddresses);
    }

    /**
     * 获取同步调用的代理对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getSyncProxy(Class<?> clazz) {
        Object proxyInstance = cachedSyncProxyInstances.get(clazz);
        if (proxyInstance != null) {
            return (T)proxyInstance;
        }
        proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},new RpcSyncProxy(pool, clazz));
        cachedSyncProxyInstances.put(clazz, proxyInstance);
        return (T) proxyInstance;
    }
}

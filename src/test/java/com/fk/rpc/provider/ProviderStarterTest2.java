package com.fk.rpc.provider;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.server.RpcServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderStarterTest2 {
    public static void main(String[] args) {
        Map<String, Object> refMap = new ConcurrentHashMap<>();
        refMap.put(HelloService.class.getName(), new HelloServiceImpl());

        RpcServer rpcServer = new RpcServer();
        rpcServer.init(8081, refMap);
    }
}

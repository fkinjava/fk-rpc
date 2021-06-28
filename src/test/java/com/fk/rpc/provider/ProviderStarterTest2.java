package com.fk.rpc.provider;

import com.fk.rpc.server.RpcServer;

public class ProviderStarterTest2 {
    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServer();
        rpcServer.init(8081);
        rpcServer.registerRef(new HelloServiceImpl());
    }
}

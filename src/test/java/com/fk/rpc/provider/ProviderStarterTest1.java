package com.fk.rpc.provider;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.server.RpcServer;


public class ProviderStarterTest1 {
    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServer();
        rpcServer.init(8084);
        rpcServer.registerRef(HelloService.class.getName(),new HelloServiceImpl());
    }

}

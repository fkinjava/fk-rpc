package com.fk.rpc.provider;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.registry.ProviderConfig;
import com.fk.rpc.registry.RpcServerRegister;
import com.fk.rpc.server.RpcServer;

public class ZkProviderTest {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.init(8080);
        HelloServiceImpl helloService = new HelloServiceImpl();
        rpcServer.registerRef(helloService);

        ProviderConfig config = ProviderConfig.builder().address("127.0.0.1:8080").interfaceClassName(HelloService.class.getName())
                .ref(helloService).build();
        RpcServerRegister rpcServerRegister = new RpcServerRegister();
        rpcServerRegister.init();
        rpcServerRegister.exportService(config);
    }
}

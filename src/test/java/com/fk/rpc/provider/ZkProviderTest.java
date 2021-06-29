package com.fk.rpc.provider;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.registry.ProviderConfig;
import com.fk.rpc.registry.RpcServerRegister;
import com.fk.rpc.server.RpcServer;

public class ZkProviderTest {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.init(8085);
        HelloServiceImpl helloService = new HelloServiceImpl();
        rpcServer.registerRef(HelloService.class.getName(),helloService);

        ProviderConfig config = ProviderConfig.builder().address("127.0.0.1:8085").interfaceClassName(HelloService.class.getName())
                .ref(helloService).build();
        RpcServerRegister rpcServerRegister = new RpcServerRegister();
        rpcServerRegister.init();
        rpcServerRegister.exportService(config);
    }
}

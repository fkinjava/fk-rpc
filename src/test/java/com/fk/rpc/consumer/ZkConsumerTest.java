package com.fk.rpc.consumer;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.client.RpcClient;
import com.fk.rpc.registry.ConsumerConfig;
import com.fk.rpc.registry.RpcClientRegister;

public class ZkConsumerTest {
    public static void main(String[] args) {
        RpcClientRegister register = new RpcClientRegister();
        ConsumerConfig config = register.getConfig(HelloService.class);
        RpcClient rpcClient = new RpcClient(config.getUrl());
        HelloService helloService = rpcClient.getSyncProxy(HelloService.class);
        String res = helloService.hello("hello rpc");
        System.out.println(res);
        String res1 = helloService.hello("hello rpc1");
        System.out.println(res1);
    }
}

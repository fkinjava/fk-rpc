package com.fk.rpc.consumer;

import com.fk.rpc.apis.HelloService;
import com.fk.rpc.client.RpcClient;

public class ConsumerStartTest {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1:8080,127.0.0.1:8081");
        HelloService helloService = rpcClient.getSyncProxy(HelloService.class);
        String res = helloService.hello("hello rpc");
        System.out.println(res);
        String res1 = helloService.hello("hello rpc1");
        System.out.println(res1);
    }

}

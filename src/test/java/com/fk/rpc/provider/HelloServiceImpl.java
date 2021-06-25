package com.fk.rpc.provider;

import com.fk.rpc.apis.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String str) {
        System.out.println(str);
        return str;
    }
}

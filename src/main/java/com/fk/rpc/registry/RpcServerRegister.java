package com.fk.rpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

@Slf4j
public class RpcServerRegister {
    private static final String IP = "127.0.0.1:2181";
    CuratorFramework client;

    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        this.client.start();
    }

    public void destroy() {
        client.close();
    }

    public void exportService(ProviderConfig providerConfig) {
        try {
            client.create().withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath("/fk-rpc/" + providerConfig.getInterfaceClassName() + "/" + "providers/" + providerConfig.getAddress());
        }catch (Exception e) {
            log.error("register provider fail,interface name is:{}", providerConfig.getInterfaceClassName(),e);
        }
    }
}

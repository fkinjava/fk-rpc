package com.fk.rpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcClientRegister {
    private static final String IP = "127.0.0.1:2181";
    CuratorFramework client;

    private Map<String, ConsumerConfig> configMap = new ConcurrentHashMap<>();

    public void init() throws Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();

        GetChildrenBuilder children = client.getChildren();

            String basePath = "/fk-rpc";
            List<String> serviceList = children.forPath(basePath);
            if (serviceList.isEmpty()) {
                return;
            }
        serviceList.forEach(service -> {
                try {
                    List<String> addrList = children.forPath(basePath + "/" + service + "/providers");
                    ConsumerConfig config = ConsumerConfig.builder().url(addrList).build();
                    configMap.put(service, config);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    public void destroy() {
        client.close();
    }

    public ConsumerConfig getConfig(Class<?> clazz) {
        return configMap.get(clazz.getName());
    }
}

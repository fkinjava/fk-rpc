package com.fk.rpc.client.connect;

import com.fk.rpc.client.handler.RpcClientHandler;
import com.fk.rpc.client.handler.RpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1、init,初始化和所有rpcServer的连接
 * 2、getConnection，分配一个连接，对已获得的连接加锁，不允许其他线程获得这个连接
 */
@Slf4j
public class ConnectionPool {

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private ReentrantLock waitForAvailableConnectionLock = new ReentrantLock();

    private Condition waitForAvailableConnectionCondition = waitForAvailableConnectionLock.newCondition();

    private AtomicInteger connectionIndex = new AtomicInteger(0);

    /**
     * 缓存已经建立的连接
     */
    private Map<InetSocketAddress, RpcConnection> cachedConnection = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<RpcConnection> connectionList = new CopyOnWriteArrayList<>();

    /**
     * 初始化连接,"127.0.0.1:8080,127.0.0.1:8081"
     */
    public void init(String addresses) {
        connectAsync(addresses);
    }

    private void connectAsync(String addresses) {
        String[] addrArray = addresses.split(",");
        for (String addr : addrArray) {
            String[] split = addr.split(":");
            InetSocketAddress socketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
            Bootstrap bs = new Bootstrap();
            bs.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new RpcClientInitializer());
            doConnectAsync(bs, socketAddress);
        }
    }

    /**
     * 发起连接的真正方法
     *
     * @param inetSocketAddress
     */
    private void doConnectAsync(Bootstrap bs, InetSocketAddress inetSocketAddress) {
        ChannelFuture channelFuture = bs.connect(inetSocketAddress);

        //连接失败时候清除资源，自动重连
        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("connect to remote fail, remote addr is:{}", inetSocketAddress);
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        doConnectAsync(bs, inetSocketAddress);
                    }
                }, 3, TimeUnit.SECONDS);

            }
        });
        //连接成功后才把连接加入连接池
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("connect to remote success, remote addr is:{}", inetSocketAddress);
                    RpcConnection rpcConnection = new DefaultRpcConnection(future.channel().pipeline().get(RpcClientHandler.class));
                    cachedConnection.put(inetSocketAddress, rpcConnection);
                    connectionList.add(rpcConnection);
                    //唤醒等待连接的线程
                    signalAllForAvailableConnection();
                }
            }
        });
    }

    /**
     * 从已建立的连接中获取一个连接
     *
     * @return
     */
    public RpcConnection getConnection() {
        CopyOnWriteArrayList<RpcConnection> connections = (CopyOnWriteArrayList<RpcConnection>) connectionList.clone();
        while (connections.size() <= 0) {
            try {
                boolean available = waitForAvailableConnection(3000);
                if (available) {
                    connections = (CopyOnWriteArrayList<RpcConnection>) connectionList.clone();
                }
            }catch (InterruptedException e) {
                log.info("wait for available connection interrupted", e);
            }
        }
        //轮询
        RpcConnection rpcConnection = connections.get(connectionIndex.getAndAdd(1) % connections.size());
        return rpcConnection;
    }

    /**
     * 等待建立连接
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    private boolean waitForAvailableConnection(int timeout) throws InterruptedException {
        waitForAvailableConnectionLock.lock();
        try {
            return waitForAvailableConnectionCondition.await(timeout, TimeUnit.MILLISECONDS);
        }  finally {
            waitForAvailableConnectionLock.unlock();
        }
    }

    /**
     * 唤醒等待连接的线程
     */
    private void signalAllForAvailableConnection() {
        waitForAvailableConnectionLock.lock();
        try {
            waitForAvailableConnectionCondition.signalAll();
        }  finally {
            waitForAvailableConnectionLock.unlock();
        }
    }
}

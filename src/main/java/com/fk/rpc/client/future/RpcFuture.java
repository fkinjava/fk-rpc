package com.fk.rpc.client.future;

import com.fk.rpc.codec.RpcRequest;
import com.fk.rpc.codec.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RpcFuture implements Future<Object> {
    private static final long TIME_THRESHOLD = 5000;

    private AtomicBoolean done;

    private ReentrantLock lock;

    private Condition condition;

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    private long startTime;

    public RpcFuture(RpcRequest request) {
        this.done = new AtomicBoolean(false);
        this.rpcRequest = request;
        this.startTime = System.currentTimeMillis();
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    public void done(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
        done.compareAndSet(false, true);
        signalWaitThreads();
        // 整体rpc调用的耗时
        long costTime = System.currentTimeMillis() - startTime;
        if (TIME_THRESHOLD < costTime) {
            log.warn("the rpc response time is too slow, request id = " + this.rpcRequest.getRequestId() + " cost time: " + costTime);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done.get();
    }

    @Override
    public Object get() throws InterruptedException {
        waitForResponse();
        if (this.rpcResponse != null) {
            return this.rpcResponse;
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        waitForResponse(timeout, unit);
        if (done.get()) {
            if (this.rpcResponse != null) {
                return this.rpcResponse;
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("timeout exception requestId: "
                    + this.rpcRequest.getRequestId()
                    + ",className: " + this.rpcRequest.getClassName()
                    + ",methodName: " + this.rpcRequest.getMethodName());
        }
    }

    private void waitForResponse() throws InterruptedException {
        lock.lock();
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
    }

    private void waitForResponse(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            condition.await(timeout, unit);
        } finally {
            lock.unlock();
        }
    }

    private void signalWaitThreads() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

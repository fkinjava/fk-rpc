package com.fk.rpc.codec;

import lombok.Data;

/**
 * @className: RpcResponse
 * @description: Rpc响应
 * @author: jurly
 * @date: 6/23/2021
 **/
@Data
public class RpcResponse {
    private String requestId;

    private Object result;

    private Throwable throwable;
}

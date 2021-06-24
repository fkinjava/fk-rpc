package com.fk.rpc.codec;

import lombok.Data;

/**
 * @className: RpcRequest
 * @description: TODO 类描述
 * @author: jurly
 * @date: 6/23/2021
 **/
@Data
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] params;

}

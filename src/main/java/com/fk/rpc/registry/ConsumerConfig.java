package com.fk.rpc.registry;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConsumerConfig {
    /**
     * 	直连调用地址
     */
    protected volatile List<String> url;

}

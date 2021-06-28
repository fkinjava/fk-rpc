package com.fk.rpc.registry;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderConfig {

    private Object ref;

    private String address;

    private String interfaceClassName;

}

package com.proj.stan.common.entity;

import lombok.*;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceProperties {

    /**
     * 服务组
     */
    private String group;

    /**
     * 服务版本
     */
    private String version;

    /**
     * 服务名
     */
    private String serviceName;

    public String toServiceName() {
        return this.serviceName + this.group + this.version;
    }
}

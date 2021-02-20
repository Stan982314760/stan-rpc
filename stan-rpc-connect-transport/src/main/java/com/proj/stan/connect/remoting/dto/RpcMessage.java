package com.proj.stan.connect.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMessage implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * messageType
     */
    private byte messageType;

    /**
     * serialization type
     */
    private byte codec;

    /**
     * compress type
     */
    private byte compress;

    /**
     * requestId
     */
    private int requestId;


    /**
     * requestData
     */
    private Object data;

}
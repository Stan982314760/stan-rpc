package com.proj.stan.connect.remoting.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
public class RpcConstant {


    /**
     * Magic number. Verify RpcMessage
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 's', (byte) 't', (byte) 'a', (byte) 'n'};

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // version
    public static final byte VERSION = 1;

    // msg type
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    public static final byte HEARTBEAT_REQUEST_TYPE = 3; // ping
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4; // pong

    public static final int HEAD_LENGTH = 16;

    public static final String PING = "ping";
    public static final String PONG = "pong";

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;


    public static final int PORT = 9999;

}

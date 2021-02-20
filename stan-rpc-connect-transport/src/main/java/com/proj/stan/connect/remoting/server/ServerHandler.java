package com.proj.stan.connect.remoting.server;

import com.proj.stan.common.enums.CompressTypeEnum;
import com.proj.stan.common.enums.RpcResponseCodeEnum;
import com.proj.stan.common.enums.SerializeTypeEnum;
import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import com.proj.stan.connect.remoting.dto.RpcMessage;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final RequestHandler requestHandler;

    public ServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                // 回复 ping
                if (RpcConstant.HEARTBEAT_REQUEST_TYPE == rpcMessage.getMessageType()) {
                    log.info("server receive PING");
                    RpcMessage responseMessage = RpcMessage.builder()
                            .compress(CompressTypeEnum.GZIP.getCode())
                            .codec(SerializeTypeEnum.KRYO.getCode())
                            .messageType(RpcConstant.HEARTBEAT_RESPONSE_TYPE)
                            .data(RpcConstant.PONG)
                            .build();
                    ctx.writeAndFlush(responseMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    // 执行方法
                    RpcRequest request = (RpcRequest) rpcMessage.getData();
                    Object result = requestHandler.handleRequest(request);
                    log.info("server invoke request is: {}", result.toString());

                    RpcMessage response = RpcMessage.builder()
                            .requestId(rpcMessage.getRequestId())
                            .compress(rpcMessage.getCompress())
                            .codec(rpcMessage.getCodec())
                            .messageType(RpcConstant.RESPONSE_TYPE)
                            .build();

                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, request.getRequestId());
                        response.setData(rpcResponse);
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        response.setData(rpcResponse);
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        log.error("not writable now, message dropped");
                    }

                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == stateEvent.state()) {
                log.info("server idle check happen, so close the client channel");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server handler error", cause);
        ctx.channel().close();
    }
}

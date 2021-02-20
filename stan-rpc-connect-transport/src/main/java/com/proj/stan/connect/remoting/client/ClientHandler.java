package com.proj.stan.connect.remoting.client;

import com.proj.stan.common.enums.CompressTypeEnum;
import com.proj.stan.common.enums.SerializeTypeEnum;
import com.proj.stan.common.factory.SingletonFactory;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import com.proj.stan.connect.remoting.dto.RpcMessage;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequestCenter requestCenter;

    public ClientHandler() {
        this.requestCenter = SingletonFactory.getInstance(UnprocessedRequestCenter.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                if (RpcConstant.HEARTBEAT_RESPONSE_TYPE == tmp.getMessageType()) {
                    log.info("client receive PONG");
                } else if (RpcConstant.RESPONSE_TYPE == tmp.getMessageType()){
                    requestCenter.complete((RpcResponse<Object>) tmp.getData());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        log.error("netty client handler error", e);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                RpcMessage rpcMessage = RpcMessage.builder()
                        .compress(CompressTypeEnum.GZIP.getCode())
                        .codec(SerializeTypeEnum.KRYO.getCode())
                        .messageType(RpcConstant.HEARTBEAT_REQUEST_TYPE)
                        .data(RpcConstant.PING)
                        .build();

                ctx.writeAndFlush(rpcMessage).addListener((ChannelFuture f) -> {
                    if (f.isSuccess()) {
                        log.info("client send PING");
                    } else {
                        log.error("client send PING error!!!");
                        ctx.channel().close();
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}

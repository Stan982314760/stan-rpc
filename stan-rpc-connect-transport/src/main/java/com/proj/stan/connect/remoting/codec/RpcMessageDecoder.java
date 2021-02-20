package com.proj.stan.connect.remoting.codec;

import com.proj.stan.common.enums.CompressTypeEnum;
import com.proj.stan.common.enums.SerializeTypeEnum;
import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.connect.compress.Compress;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import com.proj.stan.connect.remoting.dto.RpcMessage;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import com.proj.stan.connect.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p>
 *
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstant.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf msg = (ByteBuf) decode;
            if (msg.readableBytes() >= RpcConstant.HEAD_LENGTH) {
                try {
                    return decodeFrame(msg);
                } catch (Exception e) {
                    log.error("decode msg error", e);
                } finally {
                    msg.release();
                }

            }
        }

        return decode;
    }

    private Object decodeFrame(ByteBuf msg) {
        // 检查魔数
        byte[] magicArr = new byte[RpcConstant.MAGIC_NUMBER.length];
        msg.readBytes(magicArr);
        for (int i = 0; i < magicArr.length; i++) {
            if (magicArr[i] != RpcConstant.MAGIC_NUMBER[i]) {
                throw new IllegalStateException("magic number doesn't match");
            }
        }

        // 检查版本号
        byte ver = msg.readByte();
        if (ver != RpcConstant.VERSION) {
            throw new IllegalStateException("version doesn't match");
        }

        // frame总长
        int fullLength = msg.readInt();

        // msg type
        byte msgType = msg.readByte();

        // codec
        byte codecType = msg.readByte();

        // compress
        byte compressType = msg.readByte();

        // requestId
        int requestId = msg.readInt();

        RpcMessage rpcMessage = RpcMessage.builder().messageType(msgType).codec(codecType).compress(compressType).requestId(requestId).build();
        if (RpcConstant.HEARTBEAT_REQUEST_TYPE == msgType) {
            rpcMessage.setData(RpcConstant.PING);
        } else if (RpcConstant.HEARTBEAT_RESPONSE_TYPE == msgType) {
            rpcMessage.setData(RpcConstant.PONG);
        } else {
            int bodyLength = fullLength - msg.readerIndex();
            if (bodyLength > 0) {
                byte[] dst = new byte[bodyLength];
                msg.readBytes(dst);

                // decompress
                String compressName = CompressTypeEnum.getName(compressType);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                dst = compress.decompress(dst);

                // deserialize
                String serializerName = SerializeTypeEnum.getName(codecType);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerName);
                if (RpcConstant.REQUEST_TYPE == msgType) {
                    RpcRequest request = serializer.deserialize(dst, RpcRequest.class);
                    rpcMessage.setData(request);
                } else {
                    RpcResponse response = serializer.deserialize(dst, RpcResponse.class);
                    rpcMessage.setData(response);
                }
            }
        }

        return rpcMessage;
    }
}

package com.proj.stan.connect.remoting.codec;

import com.proj.stan.common.enums.CompressTypeEnum;
import com.proj.stan.common.enums.SerializeTypeEnum;
import com.proj.stan.common.extension.ExtensionLoader;
import com.proj.stan.connect.compress.Compress;
import com.proj.stan.connect.remoting.constant.RpcConstant;
import com.proj.stan.connect.remoting.dto.RpcMessage;
import com.proj.stan.connect.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

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
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {

        try {
            // magic number
            out.writeBytes(RpcConstant.MAGIC_NUMBER);
            // version
            out.writeByte(RpcConstant.VERSION);
            // full length
            out.markWriterIndex();
            out.writerIndex(out.writerIndex() + 4);
            // msg Type
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            // codec
            byte codec = rpcMessage.getCodec();
            out.writeByte(codec);
            // compress
            byte compressType = rpcMessage.getCompress();
            out.writeByte(compressType);
            // requestId
            out.writeInt(counter.incrementAndGet());

            // mark length
            int fullLength = RpcConstant.HEAD_LENGTH;

            // serialize and compress body
            byte[] body = new byte[0];

            if (RpcConstant.HEARTBEAT_REQUEST_TYPE != messageType &&
                    RpcConstant.HEARTBEAT_RESPONSE_TYPE != messageType) {

                // serialize
                String serializerName = SerializeTypeEnum.getName(codec);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializerName);
                body = serializer.serialize(rpcMessage.getData());

                // compress
                String compressName = CompressTypeEnum.getName(compressType);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                body = compress.compress(body);
            }


            if (body.length > 0) {
                fullLength += body.length;
                out.writeBytes(body);
            }


            // start write length
            int writeIndex = out.writerIndex();
            out.resetWriterIndex();
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("encode msg error", e);
        }
    }
}

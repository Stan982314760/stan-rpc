import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private AttributeKey<Integer> attributeKey = AttributeKey.valueOf("counter");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Attribute<Integer> attribute = ctx.attr(attributeKey);
        int counter = 1;
        if (attribute.get() == null) {
            attribute.set(1);
        } else {
            counter = attribute.get();
            counter++;
            attribute.set(counter);
        }
        String line = (String) msg;
        System.out.println("第" + counter + "次请求:" + line);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
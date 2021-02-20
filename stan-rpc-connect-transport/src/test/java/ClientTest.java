import com.proj.stan.connect.remoting.client.NettyClient;
import com.proj.stan.connect.remoting.constant.RpcConstant;

import java.net.InetSocketAddress;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
public class ClientTest {
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.doConnect(new InetSocketAddress("localhost", RpcConstant.PORT));
    }
}

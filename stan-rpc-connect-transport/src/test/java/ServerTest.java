import com.proj.stan.connect.remoting.server.NettyServer;

/**
 * @Author: stan
 * @Date: 2021/02/20
 * @Description:
 */
public class ServerTest {
    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.start();
    }
}

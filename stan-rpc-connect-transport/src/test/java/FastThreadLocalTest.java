import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @Author: stan
 * @Date: 2021/08/24
 * @Description:
 */
public class FastThreadLocalTest {

    static FastThreadLocal<String> fastThreadLocal = new FastThreadLocal<>();

    public static void main(String[] args) {


        new FastThreadLocalThread(() -> {
            fastThreadLocal.get();
        }).start();
    }
}

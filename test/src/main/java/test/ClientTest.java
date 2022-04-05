package test;

import client.RpcClient;
import service.HiService;

/**
 * @author DearAhri520
 * @date 2022/4/5
 */
public class ClientTest {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        HiService service = client.getProxyService(HiService.class);
        service.sayHi("111");
    }
}

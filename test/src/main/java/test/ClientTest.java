package test;

import client.RpcClient;
import service.HiService;

/**
 * @author DearAhri520
 */
public class ClientTest {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        HiService service = client.getProxyService(HiService.class);
        System.out.println(service.sayHi("111"));
    }
}

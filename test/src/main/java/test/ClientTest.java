package test;

import client.RpcClient;
import lombok.extern.slf4j.Slf4j;
import service.HiService;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DearAhri520
 */
@Slf4j
public class ClientTest {
    public static void main(String[] args) throws InterruptedException {
        RpcClient client = new RpcClient();
        HiService service = client.getProxyService(HiService.class);

        System.out.println("begin");
        System.out.println(service.sayHi("111"));
        System.out.println(service.sayHi("111"));
        System.out.println("end");
    }
}

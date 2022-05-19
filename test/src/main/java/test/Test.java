package test;

import lombok.extern.slf4j.Slf4j;
import register.ZooKeeperServiceRegistry;
import serviceinfo.ServiceInfo;

/**
 * @author DearAhri520
 * @date 2022/5/17
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        ZooKeeperServiceRegistry registry = new ZooKeeperServiceRegistry("47.104.101.168:2181");
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setIpAddress("localhost");
        serviceInfo.setPort(8080);
        serviceInfo.setInterfaceName("com.org");
        registry.register(serviceInfo);
        log.info("注册成功");
    }
}

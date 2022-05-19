package server;

import discovery.ConfigurationDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import properties.RpcServerProperties;
import register.ServiceRegistry;
import provider.RpcServiceProvider;

/**
 * @author DearAhri520
 * <p>
 * Netty 服务器启动类
 * <p>
 * properties
 * serviceRegistry
 * configurationDiscovery
 * rpcServer
 * rpcServiceProvider
 */
@Slf4j
@Order(value = 1)
public class NettyRpcServerRunner implements CommandLineRunner {
    private RpcServer server;
    private RpcServerProperties properties;
    private ServiceRegistry serviceRegistry;

    public NettyRpcServerRunner(RpcServerProperties properties,
                                ServiceRegistry serviceRegistry,
                                ConfigurationDiscovery configurationDiscovery,
                                RpcServer rpcServer,
                                RpcServiceProvider rpcServiceProvider) {
        this.properties = properties;
        this.server = rpcServer;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run(String... args) throws Exception {
        Thread t = new Thread(() -> server.start());
        t.start();
        log.info("RpcServer start at port: {}", properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                /*关闭注册中心的配置*/
                serviceRegistry.close();
            } catch (Exception e) {
                log.error("Shutdown hook close error",e);
            }
        }));
    }
}

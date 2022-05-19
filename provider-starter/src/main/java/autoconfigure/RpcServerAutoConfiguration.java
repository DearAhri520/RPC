package autoconfigure;

import discover.ZookeeperConfigurationDiscovery;
import discovery.ConfigurationDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import properties.RpcServerProperties;
import register.ServiceRegistry;
import register.ZooKeeperServiceRegistry;
import server.NettyRpcServer;
import server.NettyRpcServerRunner;
import server.RpcServer;
import provider.RpcServiceProvider;

/**
 * @author DearAhri520
 */
@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {
    @Autowired
    private RpcServerProperties properties;

    /**
     * 服务注册类
     * 如果容器当中未配置 ServiceRegistry , 则注入 ZooKeeperServiceRegistry
     * 即默认服务注册为zookeeper
     *
     * @return ZooKeeperServiceRegistry
     */
    @Bean
    @ConditionalOnMissingBean(ServiceRegistry.class)
    public ServiceRegistry serviceRegistry() {
        return new ZooKeeperServiceRegistry(properties.getRegistryAddress());
    }

    /**
     * 配置发现类
     * 如果容器当中未配置 ConfigurationDiscovery , 则注入 ZookeeperConfigurationDiscovery
     * 即默认服务注册为zookeeper
     *
     * @return ZookeeperConfigurationDiscovery
     */
    @Bean
    @ConditionalOnMissingBean(ConfigurationDiscovery.class)
    public ConfigurationDiscovery configurationDiscovery() {
        return new ZookeeperConfigurationDiscovery(properties.getRegistryAddress());
    }

    @Bean
    @ConditionalOnMissingBean(RpcServer.class)
    public RpcServer rpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean(RpcServiceProvider.class)
    public RpcServiceProvider rpcServiceProvider(@Autowired RpcServerProperties properties,
                                                 @Autowired ServiceRegistry serviceRegistry,
                                                 @Autowired ConfigurationDiscovery configurationDiscovery) {
        return new RpcServiceProvider(properties, serviceRegistry,configurationDiscovery);
    }

    @Bean
    @ConditionalOnMissingBean(NettyRpcServerRunner.class)
    public NettyRpcServerRunner nettyRpcServerStarter(@Autowired ServiceRegistry serviceRegistry,
                                                      @Autowired ConfigurationDiscovery configurationDiscovery,
                                                      @Autowired RpcServer rpcServer,
                                                      @Autowired RpcServiceProvider rpcServiceProvider) {
        return new NettyRpcServerRunner(properties, serviceRegistry, configurationDiscovery, rpcServer, rpcServiceProvider);
    }
}

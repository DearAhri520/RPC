package autoconfigure;

import discovery.ServiceDiscovery;
import discovery.ZookeeperServiceDiscovery;
import loadbalance.ConsistentHashLoadBalance;
import loadbalance.LoadBalance;
import loadbalance.RandomLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import proxy.ClientStubProxyFactory;
import servers.ServerChannelCache;

/**
 * @author DearAhri520
 */
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {
    @Autowired
    private RpcClientProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public ServerChannelCache serverCache() {
        return new ServerChannelCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory() {
        return new ClientStubProxyFactory();
    }

    @Primary
    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "ConsistentHash")
    public LoadBalance consistentHashLoadBalance() {
        return new ConsistentHashLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "Random")
    public LoadBalance randomLoadBalance() {
        return new RandomLoadBalance();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscovery serviceDiscovery() {
        return new ZookeeperServiceDiscovery();
    }
}

package autoconfigure;

import discovery.ServiceDiscover;
import discovery.ZookeeperServiceDiscover;
import loadbalance.ConsistentHashLoadBalance;
import loadbalance.LoadBalance;
import loadbalance.RandomLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import processor.RpcClientProcessor;
import properties.RpcClientProperties;
import proxy.ClientStubProxyFactory;

/**
 * @author DearAhri520
 */
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
@Slf4j
public class RpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory() {
        return new ClientStubProxyFactory();
    }

    @Primary
    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "ConsistentHash", matchIfMissing = true)
    public LoadBalance consistentHashLoadBalance() {
        return new ConsistentHashLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "Random")
    public LoadBalance randomLoadBalance() {
        return new RandomLoadBalance();
    }

    @Bean("serviceDiscover")
    @ConditionalOnMissingBean
    public ServiceDiscover serviceDiscover(@Autowired RpcClientProperties properties) {
        return new ZookeeperServiceDiscover(properties.getDiscoveryAddress());
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory,
                                                 @Autowired ServiceDiscover serviceDiscover,
                                                 @Autowired LoadBalance loadBalance,
                                                 @Autowired RpcClientProperties properties) {
        return new RpcClientProcessor(clientStubProxyFactory, serviceDiscover, loadBalance, properties);
    }
}

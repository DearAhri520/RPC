package provider;

import annotation.RpcService;
import discovery.ConfigurationDiscovery;
import properties.RpcServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import register.ServiceRegistry;
import serviceinfo.ServiceInfo;

import java.net.InetAddress;

/**
 * @author DearAhri520
 */
@Slf4j
public class RpcServiceProvider implements BeanPostProcessor {
    private RpcServerProperties properties;
    private ServiceRegistry serviceRegistry;
    /**
     * 配置发现,该类用于获取注册中心配置,暂不使用
     */
    private ConfigurationDiscovery configurationDiscovery;

    public RpcServiceProvider(RpcServerProperties properties, ServiceRegistry serviceRegistry, ConfigurationDiscovery configurationDiscovery) {
        this.properties = properties;
        this.serviceRegistry = serviceRegistry;
        this.configurationDiscovery = configurationDiscovery;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService service = bean.getClass().getAnnotation(RpcService.class);
        if (service == null) {
            return bean;
        }
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length == 0) {
            log.error("The number of interfaces for the {} is 0", bean.getClass());
            return bean;
        }
        try {
            String interfaceName = interfaces[0].getName();
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setInterfaceName(interfaceName);
            serviceInfo.setPort(properties.getPort());
            serviceInfo.setIpAddress(InetAddress.getLocalHost().getHostAddress());
            /*将服务注册入注册中心*/
            serviceRegistry.register(serviceInfo);
            log.info("Service {} is registered by registration center", serviceInfo);
            /*将服务注册入本地缓存*/
            ServiceCache.addService(interfaceName, bean);
            log.info("Service {} is registered by the cache", interfaceName);
        } catch (Exception e) {
            log.error("Service registration error", e);
        }
        return bean;
    }
}

package register;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import serviceinfo.ServiceInfo;

/**
 * @author DearAhri520
 */
@Slf4j
public class ZooKeeperServiceRegistry implements ServiceRegistry {
    /**
     * 初始sleep时间
     */
    private final int Base_Sleep_Time_Ms = 3000;

    /**
     * 最大重试次数
     */
    private final int Max_Retries = 10;

    /**
     * rpc命名空间
     */
    private static final String NAMESPACE = "/rpc/service";

    private ServiceDiscovery<ServiceInfo> serviceRegistry;

    public ZooKeeperServiceRegistry(String registryAddress) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddress, new ExponentialBackoffRetry(Base_Sleep_Time_Ms, Max_Retries));
        client.start();
        JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
        this.serviceRegistry = ServiceDiscoveryBuilder.builder(ServiceInfo.class).
                client(client).
                serializer(serializer).
                basePath(NAMESPACE).
                build();
        try {
            this.serviceRegistry.start();
        } catch (Exception e) {
            log.error("ZooKeeper service registry start error :{}", e.getMessage());
        }
        log.info("ZooKeeper service registry start success");
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder().
                name(serviceInfo.getInterfaceName()).
                address(serviceInfo.getIpAddress()).
                port(serviceInfo.getPort()).
                payload(serviceInfo).
                build();
        serviceRegistry.registerService(serviceInstance);
        log.info("ZooKeeper service registry register :{}", serviceInfo);
    }

    @Override
    public void cancel(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder().
                name(serviceInfo.getInterfaceName()).
                address(serviceInfo.getIpAddress()).
                port(serviceInfo.getPort()).
                payload(serviceInfo).
                build();
        serviceRegistry.unregisterService(serviceInstance);
        log.info("ZooKeeper service registry unregister :{}", serviceInfo);
    }

    @Override
    public void close() throws Exception {
        serviceRegistry.close();
        log.info("ZooKeeper service registry close");
    }
}

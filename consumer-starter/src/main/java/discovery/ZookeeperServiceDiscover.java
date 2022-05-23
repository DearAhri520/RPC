package discovery;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import serviceinfo.ServiceInfo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DearAhri520
 * <p>
 * zookeeper服务发现
 */
@Slf4j
public class ZookeeperServiceDiscover implements ServiceDiscover {
    /**
     * 初始sleep时间
     */
    private static final int BASE_SLEEP_TIME_MS = 3000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 10;

    /**
     * rpc命名空间
     */
    private static final String NAMESPACE = "/rpc/service";

    /**
     * 服务发现
     */
    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    public ZookeeperServiceDiscover(String registryAddress) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddress, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class).
                client(client).
                serializer(serializer).
                basePath(NAMESPACE).
                build();
        try {
            this.serviceDiscovery.start();
        } catch (Exception exception) {
            log.error("ZooKeeper服务发现启动失败 :{}", exception.getMessage());
        }
        log.info("ZooKeeper服务发现启动成功");
    }

    @Override
    public List<ServiceInfo> getAllServices(String interfaceName) throws Exception {
        Collection<ServiceInstance<ServiceInfo>> serviceInstances = serviceDiscovery.queryForInstances(interfaceName);
        return serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
    }
}

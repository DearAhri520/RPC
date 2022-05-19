package discover;

import discovery.ConfigurationDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.HashMap;
import java.util.List;

/**
 * @author DearAhri520
 * <p>
 * zookeeper 配置发现类
 */
@Slf4j
public class ZookeeperConfigurationDiscovery implements ConfigurationDiscovery {
    /**
     * 初始sleep时间
     */
    private static final int Base_Sleep_Time_Ms = 3000;

    /**
     * 最大重试次数
     */
    private static final int Max_Retries = 10;

    /**
     * rpc命名空间
     */
    private static final String NAMESPACE = "rpc";

    /**
     * session过期时间
     */
    private static final Integer SESSION_TIMEOUT_MS = 60 * 1000;

    /**
     *
     */
    private static final Integer CONNECTION_TIMEOUT_MS = 15 * 1000;

    private CuratorFramework configurationDiscovery;

    public ZookeeperConfigurationDiscovery(String registryAddress) {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(Base_Sleep_Time_Ms, Max_Retries);
            configurationDiscovery = CuratorFrameworkFactory.builder().
                    connectString(registryAddress).
                    sessionTimeoutMs(SESSION_TIMEOUT_MS).
                    connectionTimeoutMs(CONNECTION_TIMEOUT_MS).
                    retryPolicy(retryPolicy).
                    namespace(NAMESPACE).
                    build();
            this.configurationDiscovery.start();
        } catch (Exception e) {
            log.error("Zookeeper configuration discovery start error :{}", e.getMessage());
        }
    }

    /**
     * 配置发现
     *
     * @return 获取所有配置项
     */
    public HashMap<String, String> configurationsDiscovery() {
        HashMap<String, String> configs = new HashMap<>(16);
        try {
            List<String> paths;
            paths = configurationDiscovery.getChildren().forPath("/properties");
            for (String key : paths) {
                String value = new String(configurationDiscovery.getData().forPath("/properties/" + key));
                configs.put(key, value);
            }
        } catch (Exception e) {
            log.error("Configurations discovery error", e);
        }
        return configs;
    }

    @Override
    public void close() throws Exception {
        configurationDiscovery.close();
        log.info("Zookeeper configuration discovery close");
    }
}
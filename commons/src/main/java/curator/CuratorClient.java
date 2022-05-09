package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author DearAhri520
 * <p>
 * 名称空间: /rpc
 */
public class CuratorClient {
    protected CuratorFramework curatorClient;

    private String connectString;

    public CuratorClient() {
        /*加载 application.properties 文件并读取相应配置*/
        Properties properties;
        try (InputStream in = CuratorClient.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                return;
            }
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        /*默认设置为 47.104.101.168:2181*/
        this.connectString = properties.getProperty("ZooKeeperConnect", "47.104.101.168:2181");
    }

    public void connect() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        curatorClient = CuratorFrameworkFactory.builder().
                connectString(connectString).
                sessionTimeoutMs(60 * 1000).
                connectionTimeoutMs(15 * 1000).
                retryPolicy(retryPolicy).
                namespace("rpc").
                build();
        curatorClient.start();
    }

    public void close() {
        if (curatorClient != null) {
            curatorClient.close();
        }
    }
}
package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author DearAhri520
 * <p>
 * 名称空间:rpc
 */
public class CuratorClient {
    protected CuratorFramework curatorClient;

    public CuratorClient() {
        /*todo:IP:PORT 可以不用写死*/
        this("47.104.101.168:2181");
    }

    public CuratorClient(String connectString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        curatorClient = CuratorFrameworkFactory.builder().
                connectString(connectString).
                sessionTimeoutMs(60 * 1000).
                connectionTimeoutMs(15 * 1000).
                retryPolicy(retryPolicy).
                namespace("rpc").
                build();
    }

    public void connect() {
        curatorClient.start();
    }

    public void close() {
        if (curatorClient != null) {
            curatorClient.close();
        }
    }
}
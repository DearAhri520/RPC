package curator;

import org.apache.zookeeper.CreateMode;
import service.ServicesFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DearAhri520
 */
public class CuratorToServer extends CuratorClient {
    /**
     * 添加该服务器的所有服务
     */
    public void addService(String connectString) throws Exception {
        Collection<Object> proxies = ServicesFactory.allProxies();
        for (Object o : proxies) {
            /*向zookeeper中注册临时节点服务*/
            curatorClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/services/" + o.getClass().getInterfaces()[0].getName() + "/" + connectString);
        }
    }

    /**
     * 获取配置
     */
    public HashMap<String, String> getConfig() throws Exception {
        HashMap<String, String> configMap = new HashMap<>(16);
        List<String> paths = curatorClient.getChildren().forPath("/config");
        for (String key : paths) {
            String value = new String(curatorClient.getData().forPath("/config/" + key));
            configMap.put(key, value);
        }
        return configMap;
    }
}

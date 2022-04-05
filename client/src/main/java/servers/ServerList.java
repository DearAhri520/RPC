package servers;

import curator.CuratorToClient;
import lombok.extern.slf4j.Slf4j;
import message.Message;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.HashMap;

/**
 * @author DearAhri520
 * 可用的服务列表
 */
@Slf4j
public class ServerList {
    /**
     * 服务名->可用服务提供者列表
     */
    private HashMap<String, ServerProviders> map = new HashMap();

    private CuratorToClient curator;

    public ServerList() {
        this.curator = new CuratorToClient();
        curator.connect();
        curator.listen((client, event) -> {
            /*仅监听添加与删除事件*/
            if (event.getType() != PathChildrenCacheEvent.Type.CHILD_ADDED && event.getType() != PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                return;
            }
            String path = event.getData().getPath();
            /*
             * 对于 /rpc/services/HelloServices/IP:PORT
             * 分割后为 [, rpc, HelloServices, IP:PORT]
             */
            String[] paths = path.split("/");
            switch (event.getType()) {
                /*添加可用服务节点*/
                case CHILD_ADDED:
                    /*如果paths长度为3,意味着某个服务被添加*/
                    if (paths.length == 3) {
                        map.put(paths[2], new ServerProviders());
                        log.info("添加服务: {}", paths[2]);
                    }
                    /*如果paths长度为4,意味着某个服务下的IP地址被删除*/
                    else if (paths.length == 4) {
                        map.get(paths[2]).addServer(paths[3]);
                        log.info("添加服务: {} ,IP: {}", paths[2], paths[3]);
                    }
                    break;
                /*删除可用服务节点*/
                case CHILD_REMOVED:
                    /*如果paths长度为3,意味着某个服务被删除*/
                    if (paths.length == 3) {
                        map.remove(paths[2]);
                    }
                    /*如果paths长度为4,意味着某个服务下的IP地址被删除*/
                    else if (paths.length == 4) {
                        map.get(paths[2]).removeServer(paths[3]);
                    }
                    break;
                default:
                    log.error("监听到异常的子节点事件 {}", event.getType());
            }
        });
    }

    /**
     * 根据服务名 , 获取一个可用的服务器IP与端口
     *
     * @param service 服务名
     * @return IP:PORT
     */
    public String getProvider(String service, Message message) {
        ServerProviders providers = map.get(service);
        if (providers == null || providers.size() == 0) {
            throw new RuntimeException("服务:" + service + "不存在");
        }
        return providers.getServer(message);
    }
}

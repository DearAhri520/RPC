package loadbalancing;

import loadbalancing.LoadingAlgorithm;
import lombok.extern.slf4j.Slf4j;
import message.Message;
import utils.HashUtil;

import java.util.*;

/**
 * @author DearAhri520
 */
@Slf4j
public class ConsistentHashLoadingAlgorithm implements LoadingAlgorithm {
    /**
     * 真实集群列表
     */
    private List<String> realServers = new LinkedList<>();

    /**
     * 虚拟节点映射关系
     */
    private SortedMap<Integer, String> virtualNodes = new TreeMap<>();

    /**
     * 构造器
     */
    public ConsistentHashLoadingAlgorithm() {

    }

    /**
     * 获取虚拟节点名称
     *
     * @param realName 真实节点名称
     * @param num      标识符
     * @return 虚拟节点名称
     */
    private String getVirtualNodeName(String realName, int num) {
        return realName + "&&VN" + num;
    }

    /**
     * 获取真实节点名称
     *
     * @param virtualName 虚拟节点名称
     * @return 真实节点名称
     */
    private String getRealNodeName(String virtualName) {
        return virtualName.split("&&")[0];
    }

    /**
     * 获取服务器 IP:PORT
     *
     * @param message 发送的消息
     * @return 服务器IP:PORT
     */
    @Override
    public String getServer(Message message) {
        int hash = HashUtil.hash(message);
        /*只取出所有大于该hash值的部分而不必遍历整个Tree*/
        SortedMap<Integer, String> subMap = virtualNodes.tailMap(hash);
        String virtualNodeName;
        if (subMap.isEmpty()) {
            /*hash值在最尾部，应该映射到第一个group上*/
            virtualNodeName = virtualNodes.get(virtualNodes.firstKey());
        } else {
            virtualNodeName = subMap.get(subMap.firstKey());
        }
        return getRealNodeName(virtualNodeName);
    }

    /**
     * 更新虚拟节点环
     */
    private void refreshHashCircle() {
        /*当集群变动时，刷新hash环，其余的集群在hash环上的位置不会发生变动*/
        virtualNodes.clear();
        for (String realGroup : realServers) {
            /*虚拟节点五倍于真实节点*/
            for (int i = 0; i < realServers.size() * 5; i++) {
                String virtualNodeName = getVirtualNodeName(realGroup, i);
                int hash = HashUtil.hash(virtualNodeName);
                log.info("[" + virtualNodeName + "] launched @ " + hash);
                virtualNodes.put(hash, virtualNodeName);
            }
        }
    }

    /**
     * 添加服务器地址
     *
     * @param connectString 服务器 IP:PORT
     */
    @Override
    public void addServer(String connectString) {
        realServers.add(connectString);
        refreshHashCircle();
    }

    /**
     * 移除服务器地址
     *
     * @param connectString 服务器 IP:PORT
     */
    @Override
    public void removeServer(String connectString) {
        int i = 0;
        for (String group : realServers) {
            if (group.equals(connectString)) {
                realServers.remove(i);
            }
            i++;
        }
        refreshHashCircle();
    }

    @Override
    public int size() {
        return realServers.size();
    }

    @Override
    public String getName() {
        return "consistentHash";
    }
}
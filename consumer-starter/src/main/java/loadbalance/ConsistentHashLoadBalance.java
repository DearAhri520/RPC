package loadbalance;

import lombok.extern.slf4j.Slf4j;
import message.MessageBody;
import serviceinfo.ServiceInfo;
import utils.HashUtil;

import java.util.*;

/**
 * @author DearAhri520
 * <p>
 * hash一致性负载均衡
 */
@Slf4j
public class ConsistentHashLoadBalance implements LoadBalance {
    /**
     * 真实集群列表
     */
    private volatile List<ServiceInfo> realServerInfos = new LinkedList<>();

    /**
     * 虚拟节点映射关系
     */
    private volatile SortedMap<Integer, String> virtualNodes = new TreeMap<>();

    public ConsistentHashLoadBalance() {
        log.info("加载ConsistentHashLoadBalance类");
    }

    /**
     * 获取服务器 IP:PORT
     *
     * @param message 发送的消息
     * @return 服务器IP:PORT
     */
    @Override
    public ServiceInfo getServer(List<ServiceInfo> serviceInfos, MessageBody message) {
        for (ServiceInfo serviceInfo : serviceInfos) {
            addServer(serviceInfo);
        }
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
        String cs = getRealNodeName(virtualNodeName);
        String[] css = cs.split(":");

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setIpAddress(css[0]);
        serviceInfo.setPort(Integer.parseInt(css[1]));
        return serviceInfo;
    }

    /**
     * 根据真实节点名称获取虚拟节点名称
     *
     * @param realServiceInfo 真实节点名称
     * @param num             标识符
     * @return 虚拟节点名称
     */
    private String getVirtualNodeName(ServiceInfo realServiceInfo, int num) {
        return realServiceInfo.getIpAddress() + ":" + realServiceInfo.getPort() + "&&VN" + num;
    }

    /**
     * 根据虚拟节点名称获取真实节点名称
     *
     * @param virtualName 虚拟节点名称
     * @return 真实节点名称
     */
    private String getRealNodeName(String virtualName) {
        return virtualName.split("&&")[0];
    }

    /**
     * 更新虚拟节点环
     */
    private void refreshHashCircle() {
        /*当集群变动时，刷新hash环，其余的集群在hash环上的位置不会发生变动*/
        virtualNodes.clear();
        for (ServiceInfo realGroup : realServerInfos) {
            /*虚拟节点五倍于真实节点*/
            for (int i = 0; i < 5; i++) {
                String virtualNodeName = getVirtualNodeName(realGroup, i);
                int hash = HashUtil.hash(virtualNodeName);
                virtualNodes.put(hash, virtualNodeName);
            }
        }
    }

    /**
     * 添加服务器地址
     */
    private void addServer(ServiceInfo serviceInfo) {
        realServerInfos.add(serviceInfo);
        refreshHashCircle();
    }

    public int size() {
        return realServerInfos.size();
    }
}
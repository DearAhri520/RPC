package servers;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import serviceinfo.ServiceInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 */
@Slf4j
public class ServerChannelCache {
    /**
     * server连接缓存
     */
    private static Map<ServiceInfo, Channel> serverCache = new ConcurrentHashMap<>();

    public static void addServerChannel(ServiceInfo serviceInfo, Channel channel) {
        if (serverCache.get(serviceInfo) == null) {
            log.info("连接:{} 加入本地连接缓存", serviceInfo);
            serverCache.put(serviceInfo, channel);
        }
    }

    public static void removeServerChannel(ServiceInfo serviceInfo) {
        Channel channel;
        if ((channel = serverCache.get(serviceInfo)) != null) {
            channel.close();
            log.info("连接:{} 从本地连接缓存清除", serviceInfo);
            serverCache.remove(serviceInfo);
        }
    }

    public static Channel getServerChannel(ServiceInfo serviceInfo) {
        return serverCache.get(serviceInfo);
    }

    public static boolean containServerChannel(ServiceInfo serviceInfo) {
        return serverCache.containsKey(serviceInfo);
    }
}

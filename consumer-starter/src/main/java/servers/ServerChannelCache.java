package servers;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

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
    private static Map<ServerInfo, Channel> serverCache = new ConcurrentHashMap<>();

    public static void addServerChannel(ServerInfo serverInfo, Channel channel) {
        if (serverCache.get(serverInfo) == null) {
            log.info("连接:{} 加入本地连接缓存", serverInfo);
            serverCache.put(serverInfo, channel);
        }
    }

    public static void removeServerChannel(ServerInfo serverInfo) {
        Channel channel;
        if ((channel = serverCache.get(serverInfo)) != null) {
            channel.close();
            log.info("连接:{} 从本地连接缓存清除", serverInfo);
            serverCache.remove(serverInfo);
        }
    }

    public static Channel getServerChannel(ServerInfo serverInfo) {
        return serverCache.get(serverInfo);
    }

    public static boolean containServerChannel(ServerInfo serverInfo) {
        return serverCache.containsKey(serverInfo);
    }
}

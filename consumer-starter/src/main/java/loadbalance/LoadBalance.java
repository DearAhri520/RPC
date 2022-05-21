package loadbalance;

import annotation.SPI;
import message.MessageBody;
import servers.ServerInfo;

import java.util.List;

/**
 * @author DearAhri520
 * 负载均衡算法
 */
@SPI
public interface LoadBalance {
    /**
     * 从一组服务器列表中获取一个服务器
     *
     * @param addresses   服务器列表
     * @param messageBody 消息体
     * @return 单个服务器
     */
    ServerInfo getServer(List<ServerInfo> addresses, MessageBody messageBody);
}

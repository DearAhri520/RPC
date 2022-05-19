package loadbalance;

import annotation.SPI;
import message.MessageBody;

/**
 * @author DearAhri520
 * 负载均衡算法
 */
@SPI
public interface LoadBalance {
    /**
     * 从`服务器列表中获取一个服务器
     *
     * @param message 需要发送的消息
     * @return 服务器地址
     */
    String getServer(MessageBody message);

    /**
     * 根据服务器的IP与端口,添加服务器
     *
     * @param connectString 服务器IP与端口
     */
    void addServer(String connectString);

    /**
     * 根据服务器的IP与端口,移除服务器
     *
     * @param connectString 服务器IP与端口
     */
    void removeServer(String connectString);

    /**
     * 获取可用服务器个数
     *
     * @return 可用服务器个数
     */
    int size();

    /**
     * 获取算法名字
     *
     * @return 算法名称
     */
    String getName();
}

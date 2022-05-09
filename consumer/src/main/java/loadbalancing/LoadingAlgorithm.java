package loadbalancing;

import annotation.SPI;
import message.Message;

/**
 * @author DearAhri520
 * 负载均衡算法
 */
@SPI
public interface LoadingAlgorithm {
    /**
     * 获取服务器地址
     *
     * @param message 需要发送的消息
     * @return 服务器地址
     */
    String getServer(Message message);

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
     * 获取名称
     *
     * @return 算法名称
     */
    String getName();
}

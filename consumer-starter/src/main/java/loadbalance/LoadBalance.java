package loadbalance;

import annotation.SPI;
import message.MessageBody;
import serviceinfo.ServiceInfo;

import java.util.List;

/**
 * @author DearAhri520
 * 负载均衡算法
 */
@SPI
public interface LoadBalance {
    /**
     * 从一组服务列表中获取一个服务
     *
     * @param addresses   服务器列表
     * @param messageBody 消息体
     * @return 单个服务器
     */
    ServiceInfo getServer(List<ServiceInfo> addresses, MessageBody messageBody);
}

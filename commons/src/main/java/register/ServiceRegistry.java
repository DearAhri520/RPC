package register;

import serviceinfo.ServiceInfo;

/**
 * @author DearAhri520
 * 服务注册接口
 */
public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param serviceInfo 服务信息
     * @throws Exception 异常
     */
    void register(ServiceInfo serviceInfo) throws Exception;

    /**
     * 注销服务
     *
     * @param serviceInfo 服务信息
     * @throws Exception 异常
     */
    void cancel(ServiceInfo serviceInfo) throws Exception;

    /**
     * 关闭服务注册
     *
     * @throws Exception 异常
     */
    void close() throws Exception;
}

package discovery;

import serviceinfo.ServiceInfo;

/**
 * @author DearAhri520
 */
public interface ServiceDiscovery {

    /**
     * 根据服务接口发现服务
     *
     * @param interfaceName 服务接口名称
     * @return 服务信息
     * @throws Exception exception
     */
    ServiceInfo discover(String interfaceName) throws Exception;
}

package discovery;

import serviceinfo.ServiceInfo;

import java.util.List;

/**
 * @author DearAhri520
 */
public interface ServiceDiscover {
    /**
     * 根据服务接口名称发现所有可用服务
     *
     * @param interfaceName 服务接口名称
     * @return 所有可用服务信息
     * @throws Exception exception
     */
    List<ServiceInfo> getAllServices(String interfaceName) throws Exception;
}

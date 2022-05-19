package provider;

import annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DearAhri520
 */
@Slf4j
public class RPCServicesFactory {
    /**
     * 存储所有的服务
     * 接口名->代理类
     */
    private static HashMap<String, Object> servicesMap = new HashMap<>(16);

    /*
     * 加载service包下所有带有RpcAutowired注解的类,并保存
     */
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Map<String, Object> map = context.getBeansWithAnnotation(RpcService.class);
        for (Object o : map.values()) {
            servicesMap.put(o.getClass().getInterfaces()[0].getName(), o);
            log.info("扫描到服务:{}",o.getClass().getInterfaces()[0].getName());
        }
    }

    /**
     * 根据接口类名获取代理类
     *
     * @param interfaceName 接口类名
     * @return 代理类
     */
    public static Object getProxy(String interfaceName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return servicesMap.get(interfaceName);
    }

    /**
     * 获取所有代理类
     *
     * @return 所有代理类
     */
    public static Collection<Object> allProxies() {
        return servicesMap.values();
    }
}

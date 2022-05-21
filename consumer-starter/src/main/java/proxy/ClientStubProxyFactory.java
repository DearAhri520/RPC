package proxy;

import autoconfigure.RpcClientProperties;
import discovery.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 */
public class ClientStubProxyFactory {
    /**
     * 服务代理缓存
     */
    private Map<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    /**
     * 根据被代理类生成代理类对象
     *
     * @param clazz      被代理类
     * @param properties 配置文件
     * @param <T>        泛型
     * @return 生成的代理类对象
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getProxy(Class<T> clazz, RpcClientProperties properties, ServiceDiscovery serviceDiscovery) {
        Object o;
        if ((o = proxyCache.get(clazz)) == null) {
            o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ClientStubInvocationHandler(
                    clazz,
                    properties,
                    serviceDiscovery
            ));
            proxyCache.put(clazz, o);
        }
        return (T) o;
    }
}

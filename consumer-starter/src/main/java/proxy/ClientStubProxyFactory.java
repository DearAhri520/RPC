package proxy;

import properties.RpcClientProperties;
import discovery.ServiceDiscover;
import loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 */
@Slf4j
public class ClientStubProxyFactory {
    public ClientStubProxyFactory() {
        log.info("加载ClientStubProxyFactory类");
    }

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
    public <T> T getProxy(Class<T> clazz, RpcClientProperties properties, ServiceDiscover serviceDiscover, LoadBalance loadBalance) {
        Object o;
        if ((o = proxyCache.get(clazz)) == null) {
            o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ClientStubInvocationHandler(
                    clazz,
                    properties,
                    serviceDiscover,
                    loadBalance
            ));
            proxyCache.put(clazz, o);
        }
        return (T) o;
    }
}

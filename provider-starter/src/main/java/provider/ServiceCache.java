package provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DearAhri520
 *
 * 将暴露的服务缓存到本地
 * 避免重复的反射调用
 */
public class ServiceCache {
    private static final Map<String, Object> SERVICES_CACHE = new HashMap<>(16);

    public static void addService(String interfaceName, Object bean) {
        SERVICES_CACHE.put(interfaceName, bean);
    }

    public static Object getService(String interfaceName) {
        return SERVICES_CACHE.get(interfaceName);
    }

    public static Collection<Object> getAllServices() {
        return SERVICES_CACHE.values();
    }
}

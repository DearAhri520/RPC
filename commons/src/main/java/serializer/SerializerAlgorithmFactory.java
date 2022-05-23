package serializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法工厂
 */
@Slf4j
public class SerializerAlgorithmFactory {
    private static ConcurrentHashMap<Byte, SerializerAlgorithm> serializerMap;

    static {
        serializerMap = new ConcurrentHashMap<>();
        List<SerializerAlgorithm> algorithmList = SpringFactoriesLoader.loadFactories(SerializerAlgorithm.class, SerializerAlgorithmFactory.class.getClassLoader());
        /*SPI机制加载*/
        for (SerializerAlgorithm algorithm : algorithmList) {
            addSerializerAlgorithm(algorithm);
        }
    }

    /**
     * 根据标识符获取对应序列化算法
     *
     * @param type 序列化算法标识符
     * @return 序列化算法
     */
    public static SerializerAlgorithm getSerializerAlgorithm(byte type) {
        return serializerMap.get(type);
    }

    /**默认使用Proto序列化算法 {@link ProtostuffSerializerAlgorithm}*/
    public static SerializerAlgorithm getSerializerAlgorithm(String name) {
        Collection<SerializerAlgorithm> collections = serializerMap.values();
        for (SerializerAlgorithm a : collections) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return serializerMap.get((byte)2);
    }

    /**
     * 添加序列化算法
     */
    public static void addSerializerAlgorithm(SerializerAlgorithm algorithm) {
        serializerMap.put(algorithm.getIdentifier(), algorithm);
        log.info("Load serialization algorithm {}", algorithm.getName());
    }

    /**
     * 获取序列化算法个数
     */
    public static int getSize() {
        return serializerMap.size();
    }
}

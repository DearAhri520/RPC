package serializer;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法工厂
 */
public class SerializerAlgorithmFactory {
    static {
        serializerMap = new ConcurrentHashMap<>();
        addSerializerAlgorithm(new JavaSerializerAlgorithm());
        addSerializerAlgorithm(new JsonSerializerAlgorithm());
        addSerializerAlgorithm(new ProtostuffSerializerAlgorithm());
    }

    private static ConcurrentHashMap<Byte, SerializerAlgorithm> serializerMap;

    /**
     * 根据标识符获取对应序列化算法
     *
     * @param identifier 标识符
     * @return 序列化算法
     */
    public static SerializerAlgorithm getSerializerAlgorithm(byte identifier) {
        return serializerMap.get(identifier);
    }

    public static SerializerAlgorithm getSerializerAlgorithm(String name) {
        Collection<SerializerAlgorithm> collections = serializerMap.values();
        for (SerializerAlgorithm a : collections) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * 添加序列化算法
     */
    public static void addSerializerAlgorithm(SerializerAlgorithm algorithm) {
        serializerMap.put(algorithm.getIdentifier(), algorithm);
    }

    /**
     * 获取序列化算法个数
     */
    public static int getSize() {
        return serializerMap.size();
    }
}

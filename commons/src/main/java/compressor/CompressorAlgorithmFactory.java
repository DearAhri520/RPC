package compressor;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法工厂
 */
public class CompressorAlgorithmFactory {
    static {
        compressorMap = new ConcurrentHashMap<>();
        addSerializerAlgorithm(new DefaultCompressorAlgorithm());
        addSerializerAlgorithm(new GzipCompressorAlgorithm());
    }

    private static ConcurrentHashMap<Byte, CompressorAlgorithm> compressorMap;

    /**
     * 根据标识符获取对应序列化算法
     *
     * @param identifier 标识符
     * @return 序列化算法
     */
    public static CompressorAlgorithm getCompressorAlgorithm(byte identifier) {
        return compressorMap.get(identifier);
    }

    public static CompressorAlgorithm getCompressorAlgorithm(String name) {
        Collection<CompressorAlgorithm> collections = compressorMap.values();
        for (CompressorAlgorithm a : collections) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * 添加序列化算法
     */
    public static void addSerializerAlgorithm(CompressorAlgorithm algorithm) {
        compressorMap.put(algorithm.getIdentifier(), algorithm);
    }

    /**
     * 获取序列化算法个数
     */
    public static int getSize() {
        return compressorMap.size();
    }
}

package compressor;

import lombok.extern.slf4j.Slf4j;
import spi.RpcFactoriesLoader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法工厂
 */
@Slf4j
public class CompressionAlgorithmFactory {
    /**
     * 压缩算法
     */
    private static ConcurrentHashMap<Byte, CompressionAlgorithm> compressorMap;

    static {
        compressorMap = new ConcurrentHashMap<>();
        List<CompressionAlgorithm> algorithmList = RpcFactoriesLoader.loadFactories(CompressionAlgorithm.class, CompressionAlgorithmFactory.class.getClassLoader());
        /*SPI机制加载*/
        for (int i = 0; i < algorithmList.size(); i++) {
            addCompressorAlgorithm(algorithmList.get(i));
        }
    }

    /**
     * 根据标识符获取对应序列化算法
     *
     * @param identifier 标识符
     * @return 序列化算法
     */
    public static CompressionAlgorithm getCompressorAlgorithm(byte identifier) {
        return compressorMap.get(identifier);
    }

    public static CompressionAlgorithm getCompressorAlgorithm(String name) {
        Collection<CompressionAlgorithm> collections = compressorMap.values();
        for (CompressionAlgorithm a : collections) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return compressorMap.get(CompressorType.Gzip.getType());
    }

    /**
     * 添加序列化算法
     */
    public static void addCompressorAlgorithm(CompressionAlgorithm algorithm) {
        CompressionAlgorithm ca;
        if ((ca = compressorMap.get(algorithm.getIdentifier())) != null) {
            log.warn("压缩算法:{} 唯一标识符 与 {} 冲突,该压缩算法不会被加载", algorithm.getName(), ca.getName());
            return;
        }
        compressorMap.put(algorithm.getIdentifier(), algorithm);
        log.info("加载压缩算法 {}", algorithm.getName());
    }

    /**
     * 获取序列化算法个数
     */
    public static int getSize() {
        return compressorMap.size();
    }
}

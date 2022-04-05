package compressor;

import lombok.extern.slf4j.Slf4j;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;
import spi.FactoriesLoader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 * <p>
 * 序列化算法工厂
 */
@Slf4j
public class CompressorAlgorithmFactory {
    static {
        compressorMap = new ConcurrentHashMap<>();
        List<CompressorAlgorithm> algorithmList = FactoriesLoader.loadFactories(CompressorAlgorithm.class, CompressorAlgorithmFactory.class.getClassLoader());
        /*SPI机制加载*/
        for (int i = 0; i < algorithmList.size(); i++) {
            addCompressorAlgorithm(algorithmList.get(i));
        }
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
    public static void addCompressorAlgorithm(CompressorAlgorithm algorithm) {
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

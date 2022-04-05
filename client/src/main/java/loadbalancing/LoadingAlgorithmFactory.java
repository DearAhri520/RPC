package loadbalancing;

import lombok.extern.slf4j.Slf4j;
import spi.FactoriesLoader;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DearAhri520
 */
@Slf4j
public class LoadingAlgorithmFactory {
    private static ConcurrentHashMap<String, LoadingAlgorithm> loadingMap;

    static {
        loadingMap = new ConcurrentHashMap<>();
        List<LoadingAlgorithm> algorithmList = FactoriesLoader.loadFactories(LoadingAlgorithm.class, LoadingAlgorithmFactory.class.getClassLoader());
        /*SPI机制加载*/
        for (int i = 0; i < algorithmList.size(); i++) {
            addLoadingAlgorithm(algorithmList.get(i));
        }
    }

    public static LoadingAlgorithm getLoadingAlgorithm(String name) {
        return loadingMap.get(name);
    }

    /**
     * 添加序列化算法
     */
    public static void addLoadingAlgorithm(LoadingAlgorithm algorithm) {
        loadingMap.put(algorithm.getName(), algorithm);
        log.info("加载压缩算法 {}", algorithm.getName());
    }

    /**
     * 获取序列化算法个数
     */
    public static int getSize() {
        return loadingMap.size();
    }
}

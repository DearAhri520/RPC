package config;

import compressor.CompressorAlgorithm;
import compressor.CompressorAlgorithmFactory;
import curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;
import loadbalancing.LoadingAlgorithm;
import loadbalancing.LoadingAlgorithmFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author DearAhri520
 */
@Slf4j
public class ClientConfig implements Config {
    public ClientConfig() {
        Properties properties;
        try (InputStream in = CuratorClient.class.getResourceAsStream("/client.properties")) {
            if (in == null) {
                return;
            }
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        String v;
        /*从配置文件获取客户端配置*/
        if ((v = properties.getProperty("compressorAlgorithm")) != null) {
            compressorAlgorithm = CompressorAlgorithmFactory.getCompressorAlgorithm(v);
        }
        if ((v = properties.getProperty("serializerAlgorithm")) != null) {
            serializerAlgorithm = SerializerAlgorithmFactory.getSerializerAlgorithm(v);
        }
        if ((v = properties.getProperty("loadingAlgorithm")) != null) {
            loadingAlgorithm = LoadingAlgorithmFactory.getLoadingAlgorithm(v);
        }
        if ((v = properties.getProperty("minCompressLength")) != null) {
            minCompressLength = Integer.parseInt(v);
        }
        log.info("压缩算法设置 {}", this.compressorAlgorithm.getName());
        log.info("序列化算法设置 {}", this.serializerAlgorithm.getName());
        log.info("负载均衡算法设置 {}", this.loadingAlgorithm.getName());
        log.info("最小压缩消息长度设置 {}", this.minCompressLength);
    }

    /**
     * 压缩算法
     */
    private CompressorAlgorithm compressorAlgorithm = CompressorAlgorithmFactory.getCompressorAlgorithm("Gzip");

    /**
     * 序列化算法
     */
    private SerializerAlgorithm serializerAlgorithm = SerializerAlgorithmFactory.getSerializerAlgorithm("Java");

    /**
     * 负载均衡算法
     */
    private LoadingAlgorithm loadingAlgorithm = LoadingAlgorithmFactory.getLoadingAlgorithm("consistentHash");

    /**
     * 最小压缩消息长度
     */
    private int minCompressLength = 30;

    /**
     * 获取序列化算法
     *
     * @return 序列化算法
     */
    @Override
    public SerializerAlgorithm getSerializerAlgorithm() {
        return serializerAlgorithm;
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    @Override
    public int getMinCompressLength() {
        return minCompressLength;
    }

    /**
     * 获取压缩算法
     *
     * @return 压缩算法
     */
    @Override
    public CompressorAlgorithm getCompressAlgorithm() {
        return compressorAlgorithm;
    }

    /**
     * 获取负载均衡算法
     *
     * @return 负载均衡
     */
    public LoadingAlgorithm getLoadingAlgorithm() {
        return loadingAlgorithm;
    }
}
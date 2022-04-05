package config;

import compressor.CompressorAlgorithm;
import compressor.CompressorAlgorithmFactory;
import lombok.extern.slf4j.Slf4j;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;

import java.util.HashMap;

/**
 * @author DearAhri520
 * <p>
 * 服务器配置
 */
@Slf4j
public class ServerConfig implements Config {
    public ServerConfig() {
    }

    /**
     * 本机服务器IP地址
     */
    private String IPAddress = "localhost";

    /**
     * 本机服务器端口
     */
    private int port = 8080;

    /**
     * 压缩算法,默认为Gzip压缩算法
     */
    private CompressorAlgorithm compressorAlgorithm = CompressorAlgorithmFactory.getCompressorAlgorithm("Gzip");

    /**
     * 序列化算法,默认为Java自带的序列化算法
     */
    private SerializerAlgorithm serializerAlgorithm = SerializerAlgorithmFactory.getSerializerAlgorithm("Java");

    /**
     * 最小压缩消息长度
     */
    private int minCompressLength = 30;


    public String getSelfIPAddress() {
        return IPAddress;
    }

    public int getSelfPort() {
        return port;
    }

    @Override
    public SerializerAlgorithm getSerializerAlgorithm() {
        return serializerAlgorithm;
    }

    @Override
    public int getMinCompressLength() {
        return minCompressLength;
    }

    @Override
    public CompressorAlgorithm getCompressAlgorithm() {
        return compressorAlgorithm;
    }

    public void setCompressorAlgorithm(CompressorAlgorithm compressorAlgorithm) {
        this.compressorAlgorithm = compressorAlgorithm;
    }

    public void setSerializerAlgorithm(SerializerAlgorithm serializerAlgorithm) {
        this.serializerAlgorithm = serializerAlgorithm;
    }

    public void setMinCompressLength(int minCompressLength) {
        this.minCompressLength = minCompressLength;
    }

    public void setConfig(HashMap<String, String> configMap) {
        String v;
        if ((v = configMap.get("compressor_algorithm")) != null) {
            CompressorAlgorithm algorithm = CompressorAlgorithmFactory.getCompressorAlgorithm(v);
            if (algorithm != null) {
                this.compressorAlgorithm = algorithm;
            }
        }
        if ((v = configMap.get("serializer_algorithm")) != null) {
            SerializerAlgorithm algorithm = SerializerAlgorithmFactory.getSerializerAlgorithm(v);
            if (algorithm != null) {
                this.serializerAlgorithm = algorithm;
            }
        }
        if ((v = configMap.get("min_compress_length")) != null) {
            this.minCompressLength = Integer.parseInt(v);
        }
        log.info("压缩算法设置 {}", this.compressorAlgorithm.getName());
        log.info("序列化算法设置 {}", this.serializerAlgorithm.getName());
        log.info("最小压缩消息长度设置 {}", this.minCompressLength);
    }
}
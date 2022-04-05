package config;

import compressor.CompressorAlgorithm;
import compressor.CompressorAlgorithmFactory;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;

import java.util.HashMap;

/**
 * @author DearAhri520
 * <p>
 * 服务器配置
 */
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
    private int PORT = 8080;

    /**
     * 压缩算法,默认为Gzip压缩算法
     */
    private CompressorAlgorithm COMPRESSOR_ALGORITHM = CompressorAlgorithmFactory.getCompressorAlgorithm("Gzip");

    /**
     * 序列化算法,默认为Java自带的序列化算法
     */
    private SerializerAlgorithm SERIALIZER_ALGORITHM = SerializerAlgorithmFactory.getSerializerAlgorithm("Java");

    /**
     * 最小压缩消息长度
     */
    private int MIN_COMPRESS_LENGTH = 30;


    public String getSelfIPAddress() {
        return IPAddress;
    }

    public int getSelfPort() {
        return PORT;
    }

    @Override
    public SerializerAlgorithm getSerializerAlgorithm() {
        return SERIALIZER_ALGORITHM;
    }

    @Override
    public int getMinCompressLength() {
        return MIN_COMPRESS_LENGTH;
    }

    @Override
    public CompressorAlgorithm getCompressAlgorithm() {
        return COMPRESSOR_ALGORITHM;
    }

    public void setCompressorAlgorithm(CompressorAlgorithm compressorAlgorithm) {
        COMPRESSOR_ALGORITHM = compressorAlgorithm;
    }

    public void setSerializerAlgorithm(SerializerAlgorithm serializerAlgorithm) {
        SERIALIZER_ALGORITHM = serializerAlgorithm;
    }

    public void setMinCompressLength(int minCompressLength) {
        MIN_COMPRESS_LENGTH = minCompressLength;
    }

    public void setConfig(HashMap<String, String> configMap) {
        String v;
        if ((v = configMap.get("compressor_algorithm")) != null) {
            CompressorAlgorithm algorithm = CompressorAlgorithmFactory.getCompressorAlgorithm(v);
            if (algorithm != null) {
                this.COMPRESSOR_ALGORITHM = algorithm;
            }
        }
        if ((v = configMap.get("serializer_algorithm")) != null) {
            SerializerAlgorithm algorithm = SerializerAlgorithmFactory.getSerializerAlgorithm(v);
            if (algorithm != null) {
                this.SERIALIZER_ALGORITHM = algorithm;
            }
        }
        if ((v = configMap.get("min_compress_length")) != null) {
            this.MIN_COMPRESS_LENGTH = Integer.parseInt(v);
        }
    }
}
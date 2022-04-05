package config;

import compressor.CompressorAlgorithm;
import compressor.CompressorAlgorithmFactory;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;

/**
 * @author DearAhri520
 * @date 2022/3/26
 */
public class ClientConfig implements Config {
    /**
     * 压缩算法
     */
    private CompressorAlgorithm COMPRESSOR_ALGORITHM = CompressorAlgorithmFactory.getCompressorAlgorithm("Gzip");

    /**
     * 序列化算法
     */
    private SerializerAlgorithm SERIALIZER_ALGORITHM = SerializerAlgorithmFactory.getSerializerAlgorithm("Java");

    /**
     * 最小压缩消息长度
     */
    private int MIN_COMPRESS_LENGTH = 30;

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public String getServerIPAddress() {
        return "localhost";
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public int getServerPort() {
        return 8080;
    }

    /**
     * 获取序列化算法
     *
     * @return 序列化算法
     */
    @Override
    public SerializerAlgorithm getSerializerAlgorithm() {
        return SERIALIZER_ALGORITHM;
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    @Override
    public int getMinCompressLength() {
        return MIN_COMPRESS_LENGTH;
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    @Override
    public CompressorAlgorithm getCompressAlgorithm() {
        return COMPRESSOR_ALGORITHM;
    }
}
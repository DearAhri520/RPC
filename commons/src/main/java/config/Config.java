package config;

import compressor.CompressorAlgorithm;
import serializer.SerializerAlgorithm;

/**
 * @author DearAhri520
 */
public interface Config {
    /**
     * 获取序列化算法
     *
     * @return 序列化算法
     */
    SerializerAlgorithm getSerializerAlgorithm();

    /**
     * 获取压缩算法
     *
     * @return 压缩算法
     */
    CompressorAlgorithm getCompressAlgorithm();

    /**
     * 获取最小压缩消息长度
     *
     * @return 最小压缩消息长度
     */
    int getMinCompressLength();
}

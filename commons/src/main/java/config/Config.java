package config;

import compressor.CompressorAlgorithm;
import serializer.SerializerAlgorithm;

/**
 * @author DearAhri520
 * @date 2022/3/26
 */
public abstract class Config {
    /**
     * 压缩算法
     */
    private static final CompressorAlgorithm COMPRESSOR_ALGORITHM;

    /**
     * 序列化算法
     */
    private static final SerializerAlgorithm SERIALIZER_ALGORITHM;

    /**
     * 最小压缩消息长度
     */
    private static final int MIN_COMPRESS_LENGTH;


    static {
        String value;
        /*压缩算法*/
        value = null;
        if (value == null) {
            COMPRESSOR_ALGORITHM = CompressorAlgorithm.Gzip;
        } else {
            COMPRESSOR_ALGORITHM = CompressorAlgorithm.valueOf(value);
        }
        /*序列化算法*/
        value = null;
        if (value == null) {
            SERIALIZER_ALGORITHM = SerializerAlgorithm.Java;
        } else {
            SERIALIZER_ALGORITHM = SerializerAlgorithm.valueOf(value);
        }
        /*最小压缩消息长度*/
        value = null;
        if (value == null) {
            MIN_COMPRESS_LENGTH = 30;
        } else {
            MIN_COMPRESS_LENGTH = Integer.parseInt(value);
        }
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public static String getServerIPAddress() {
        return "localhost";
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public static int getServerPort() {
        return 8080;
    }

    /**
     * 获取序列化算法
     *
     * @return 序列化算法
     */
    public static SerializerAlgorithm getSerializerAlgorithm() {
        return SERIALIZER_ALGORITHM;
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    public static int getMinCompressLength() {
        return MIN_COMPRESS_LENGTH;
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    public static CompressorAlgorithm getCompressAlgorithm() {
        return COMPRESSOR_ALGORITHM;
    }
}
package RPC.config;

import RPC.compressor.CompressorAlgorithm;
import RPC.serializer.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author DearAhri520
 * @date 2022/3/26
 */
public abstract class Config {
    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public static String getServerIPAddress() {
        String value = properties.getProperty("server.ip");
        if (value == null) {
            return "localhost";
        } else {
            return value;
        }
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if (value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取序列化算法
     *
     * @return 序列化算法
     */
    public static SerializerAlgorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return SerializerAlgorithm.Java;
        } else {
            return SerializerAlgorithm.valueOf(value);
        }
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    public static int getMinCompressLength() {
        String value = properties.getProperty("compress.minCompressLength");
        if (value == null) {
            return 30;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 获取压缩最小消息长度,当消息体小于该长度时,不压缩消息
     *
     * @return 压缩最小消息长度
     */
    public static CompressorAlgorithm getCompressAlgorithm() {
        String value = properties.getProperty("compress.algorithm");
        if (value == null) {
            return CompressorAlgorithm.Gzip;
        } else {
            return CompressorAlgorithm.valueOf(value);
        }
    }
}
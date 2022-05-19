package autoconfigure;

import compressor.CompressionAlgorithm;
import compressor.CompressionAlgorithmFactory;
import loadbalance.LoadBalance;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import serializer.SerializerAlgorithm;
import serializer.SerializerAlgorithmFactory;
import loadbalance.LoadingAlgorithmFactory;

/**
 * @author DearAhri520
 */
@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {
    /**
     * 最小压缩消息长度
     */
    private int minCompressLength = 30;

    /**
     * 服务发现中心地址
     */
    private String discoveryAddress = "47.104.101.168:2181";

    /**
     * 服务调用超时时间毫秒
     */
    private Integer timeout = 1000;
}
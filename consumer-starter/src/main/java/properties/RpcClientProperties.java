package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private Integer timeout = 10000;

    /**
     * 序列化算法
     */
    private String serializer = "Proto";

    /**
     * 压缩算法
     */
    private String compressor = "Gzip";

    /**
     * 负载均衡算法
     */
    private String balance = "ConsistentHash";
}
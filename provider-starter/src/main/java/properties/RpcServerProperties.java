package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author DearAhri520
 */
@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {

    /**
     * 本机服务器IP地址
     */
    private String IPAddress = "localhost";

    /**
     * 本机服务器端口
     */
    private int port = 8080;

    /**
     * 注册服务中心
     * IP:PORT
     */
    private String registryAddress = "47.104.101.168:2181";
}

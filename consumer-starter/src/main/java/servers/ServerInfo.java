package servers;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author DearAhri520
 * <p>
 * 单个服务器
 */
@Data
@AllArgsConstructor
public class ServerInfo {
    /**
     * ip地址
     */
    private String IpAddress;

    /**
     * 端口
     */
    private Integer port;
}

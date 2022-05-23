package serviceinfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author DearAhri520
 * <p>
 * 服务信息类
 */
@Data
@ToString
@EqualsAndHashCode
public class ServiceInfo {
    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 服务接口名称
     */
    private String interfaceName;
}

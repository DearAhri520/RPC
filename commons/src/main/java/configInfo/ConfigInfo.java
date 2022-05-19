package configInfo;

import lombok.Data;
import lombok.ToString;

/**
 * @author DearAhri520
 * <p>
 * 配置信息类
 */
@Data
@ToString
public class ConfigInfo {
    /**
     * 配置key
     */
    private String key;

    /**
     * 配置value
     */
    private String value;
}

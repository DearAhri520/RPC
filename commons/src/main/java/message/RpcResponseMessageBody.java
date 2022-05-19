package message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author DearAhri520
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RpcResponseMessageBody extends MessageBody {
    /**
     * 返回值
     */
    private Object returnValue;
}
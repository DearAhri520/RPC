package message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author DearAhri520
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ResponseMessageBody extends MessageBody {
    /**
     * 返回值
     */
    private Object returnValue;
}
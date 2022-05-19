package message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author DearAhri520
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PingMessageBody extends MessageBody {
    String message;
}

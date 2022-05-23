package message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author DearAhri520
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PongMessageBody extends MessageBody {
    private String message;
}

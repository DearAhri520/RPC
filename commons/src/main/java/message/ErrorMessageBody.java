package message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author DearAhri520
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ErrorMessageBody extends MessageBody {
    private Exception e;
}

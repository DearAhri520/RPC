package message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DearAhri520
 */
@Data
public abstract class MessageBody implements Serializable {
    private static final Map<Byte, Class<?>> MESSAGE_CLASSES = new HashMap<>();

    public static Class<?> getMessageClass(byte messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    static {
        MESSAGE_CLASSES.put(MessageType.RequestMessage.getMessageType(), RequestMessageBody.class);
        MESSAGE_CLASSES.put(MessageType.ResponseMessage.getMessageType(), ResponseMessageBody.class);
        MESSAGE_CLASSES.put(MessageType.PingMessage.getMessageType(), PingMessageBody.class);
        MESSAGE_CLASSES.put(MessageType.PongMessage.getMessageType(), PongMessageBody.class);
        MESSAGE_CLASSES.put(MessageType.ErrorMessage.getMessageType(), ErrorMessageBody.class);
    }
}

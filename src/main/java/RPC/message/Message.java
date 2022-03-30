package RPC.message;

import RPC.protocol.MessageType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DearAhri520
 */
@Data
public abstract class Message implements Serializable {

    public static Class<?> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    private int sequenceId;

    protected int messageType;

    /**
     * 获取消息类型
     *
     * @return 消息类型
     */
    public abstract int getMessageType();

    private static final Map<Integer, Class<?>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(MessageType.RpcRequestMessage.getMessageType(), RpcRequestMessage.class);
        MESSAGE_CLASSES.put(MessageType.RpcResponseMessage.getMessageType(), RpcResponseMessage.class);
        MESSAGE_CLASSES.put(MessageType.PingMessage.getMessageType(), PingMessage.class);
        MESSAGE_CLASSES.put(MessageType.PongMessage.getMessageType(), PongMessage.class);
    }
}

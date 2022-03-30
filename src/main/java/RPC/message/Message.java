package RPC.message;

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
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();


    public static final int PING_MESSAGE = 14;
    public static final int PONG_MESSAGE = 15;

    /**
     * rpc请求消息
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    /**
     * rpc响应消息
     */
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 102;
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}

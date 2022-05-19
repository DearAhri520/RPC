package protocol;

/**
 * @author DearAhri520
 */
public enum MessageType {
    /**
     * PingMessage(1):心跳包请求消息
     * PongMessage(2):心跳包响应消息
     * RpcRequestMessage(101):rpc请求消息
     * RpcResponseMessage(102):rpc响应消息
     */
    PingMessage((byte) 1),
    PongMessage((byte) 2),
    RpcRequestMessage((byte) 100),
    RpcResponseMessage((byte) 101),
    ErrorMessage((byte) 200);

    /**
     * 消息类型
     */
    byte messageType;

    MessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getMessageType() {
        return messageType;
    }

    public static MessageType findByType(byte type) {
        for (MessageType msgType : MessageType.values()) {
            if (msgType.getMessageType() == type) {
                return msgType;
            }
        }
        return null;
    }
}

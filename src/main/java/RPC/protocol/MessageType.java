package RPC.protocol;

/**
 * @author DearAhri520
 * @date 2022/3/30
 */
public enum MessageType {
    /**
     * PingMessage:心跳包发送消息
     * PongMessage:心跳包响应消息
     * RpcRequestMessage(101):rpc请求消息
     * RpcResponseMessage(102):rpc响应消息
     */
    PingMessage(1),
    PongMessage(2),
    RpcRequestMessage(101),
    RpcResponseMessage(102);

    /**
     * 消息类型
     */
    int messageType;

    MessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}

package RPC.message;

import RPC.protocol.MessageType;

/**
 * @author DearAhri520
 * @date 2022/3/23
 */
public class PingMessage extends Message {
    public PingMessage() {
        messageType = MessageType.PingMessage.getMessageType();
    }

    @Override
    public int getMessageType() {
        return messageType;
    }
}

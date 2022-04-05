package message;

import protocol.MessageType;

/**
 * @author DearAhri520
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

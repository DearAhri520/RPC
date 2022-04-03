package message;

import protocol.MessageType;

/**
 * @author DearAhri520
 * @date 2022/3/23
 */
public class PongMessage extends Message {
    public PongMessage() {
        messageType = MessageType.PongMessage.getMessageType();
    }

    @Override
    public int getMessageType() {
        return messageType;
    }
}

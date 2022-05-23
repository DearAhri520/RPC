package utils;

import message.*;
import message.MessageType;
import protocol.ProtocolConstants;

/**
 * @author DearAhri520
 */
public class MessageUtils {
    public static MessageProtocol newPingMessage() {
        MessageProtocol messageProtocol = new MessageProtocol();
        MessageHeader header = new MessageHeader(
                ProtocolConstants.MAGIC_NUMBER,
                ProtocolConstants.VERSION,
                MessageType.PingMessage.getMessageType(),
                0,
                (byte) 0,
                (byte) 0,
                0);
        MessageBody body = new PingMessageBody("PING");
        messageProtocol.setHeader(header);
        messageProtocol.setBody(body);
        return messageProtocol;
    }

    public static MessageProtocol newPongMessage() {
        MessageProtocol messageProtocol = new MessageProtocol();
        MessageHeader header = new MessageHeader(
                ProtocolConstants.MAGIC_NUMBER,
                ProtocolConstants.VERSION,
                MessageType.PongMessage.getMessageType(),
                0,
                (byte) 0,
                (byte) 0,
                0);
        MessageBody body = new PongMessageBody("PONG");
        messageProtocol.setHeader(header);
        messageProtocol.setBody(body);
        return messageProtocol;
    }
}

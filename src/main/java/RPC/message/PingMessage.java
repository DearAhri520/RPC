package RPC.message;

/**
 * @author DearAhri520
 * @date 2022/3/23
 */
public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PING_MESSAGE;
    }
}

package RPC.message;

/**
 * @author DearAhri520
 * @date 2022/3/23
 */
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PONG_MESSAGE;
    }
}

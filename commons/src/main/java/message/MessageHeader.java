package message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author DearAhri520
 * <p>
 * 消息头
 */
@Data
@AllArgsConstructor
public class MessageHeader {
    /**
     * 魔数
     */
    private int magicNum;

    /**
     * 消息版本
     */
    private byte version;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 消息序列号
     */
    private int sequenceId;

    /**
     * 消息体序列化方式
     */
    private byte serializer;

    /**
     * 消息体压缩方式
     */
    private byte compress;

    /**
     * 消息体长度
     */
    private int bodyLength;
}

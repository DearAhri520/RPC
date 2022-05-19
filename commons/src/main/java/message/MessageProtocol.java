package message;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DearAhri520
 * <p>
 * 消息协议类,用于服务端和客户端之间的消息传递
 * <p>
 * -消息头
 * --魔数:4字节
 * --消息版本:1字节
 * --消息类型:1字节
 * --消息序列号:4字节
 * --消息体序列化方式:1字节
 * --消息体压缩方式:1字节
 * --消息体长度:4字节
 * -消息体
 * --长度不定
 * <p>
 * 消息最小长度为消息头长度 : 4+1+1+1+4+1+4 = 16字节
 */
@Slf4j
@Data
public class MessageProtocol {
    /**
     * 消息头
     */
    private MessageHeader header;

    /**
     * 消息体
     */
    private MessageBody body;

}
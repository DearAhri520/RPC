package RPC.protocol;

import RPC.config.Config;
import RPC.message.Message;
import RPC.serializer.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author DearAhri520
 * 协议解析
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接受到的 ByteBuf 消息是完整的
 *
 * |魔数:4字节
 * |编解码版本:1字节
 * |序列化方式:1字节
 * |
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        /*1. 4 字节的魔数 ,转换成int即为 387383298 */
        out.writeBytes(new byte[]{23, 23, 0, 2});
        /*2. 1 字节的编解码版本*/
        out.writeByte(1);
        /*3. 1 字节的序列化方式 jdk 0 , json 1 , protostuff 2*/
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        /*4. 1 字节的指令类型*/
        out.writeByte(msg.getMessageType());
        /*5. 4 个字节*/
        out.writeInt(msg.getSequenceId());
        /* 无意义，对齐填充*/
        out.writeByte(0xff);
        /* 6. 将对象序列化为字节数组*/
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        /* 7. 长度*/
        out.writeInt(bytes.length);
        /* 8. 写入内容*/
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = 387383298;

        int magicNum = in.readInt();
        /*魔数:387383298,转换成byte数组即为{23,23,0,2}*/
        if (magicNum != magic) {
            log.error("消息{}反序列化失败,魔数错误", in.array());
            throw new RuntimeException("消息反序列化失败");
        }
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        /*读取消息体长度*/
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Message message;
        if (serializerAlgorithm < SerializerAlgorithm.values().length && serializerAlgorithm >= 0) {
            /*获取反序列化算法*/
            SerializerAlgorithm algorithm = SerializerAlgorithm.values()[serializerAlgorithm];
            /*获取具体的message类型*/
            Class<?> messageClass = Message.getMessageClass(messageType);
            message = (Message) algorithm.deserialize(messageClass, bytes);
        } else {
            log.error("消息{}反序列化失败,编解码算法ID错误", in.array());
            throw new RuntimeException("消息反序列化失败");
        }
        out.add(message);
    }
}

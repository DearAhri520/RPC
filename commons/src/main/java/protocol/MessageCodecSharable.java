package protocol;

import compressor.CompressorAlgorithm;
import config.Config;
import message.Message;
import serializer.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author DearAhri520
 * @author DearAhri520
 * 协议解析
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接受到的 ByteBuf 消息是完整的
 * <p>
 * |魔数:4字节
 * |编解码版本:1字节
 * |序列化方式:1字节
 * |指令消息类型:1字节
 * |消息序列号:4字节
 * |压缩标志:1字节
 * |消息体长度:4字节
 * (消息最小长度:4+1+1+1+4+1+4 = 16字节)
 * |消息体:长度不定
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws IOException {
        ByteBuf out = ctx.alloc().buffer();
        /*1. 4 字节的魔数 ,转换成int即为 387383298 */
        out.writeBytes(new byte[]{23, 23, 0, 2});
        /*2. 1 字节的编解码版本*/
        out.writeByte(1);
        /*3. 1 字节的序列化方式 jdk 0 , json 1 , protostuff 2*/
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        /*4. 1 字节的指令类型*/
        out.writeByte(msg.getMessageType());
        /*5. 4 字节的消息序列号*/
        out.writeInt(msg.getSequenceId());
        /*6. 将对象序列化为字节数组*/
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        CompressorAlgorithm compressAlgorithm = Config.getCompressAlgorithm();
        /*如果消息体达到压缩最小消息长度,则对消息体进行压缩,并获取压缩后的消息*/
        if (bytes.length >= Config.getMinCompressLength()) {
            bytes = compressAlgorithm.compress(bytes);
            /*7. 1 字节的压缩标志*/
            out.writeByte(Config.getCompressAlgorithm().ordinal());
            System.out.println(Config.getCompressAlgorithm().ordinal());
        } else {
            /*7. 1 字节的压缩标志*/
            out.writeByte(0);
        }
        /*8. 4 字节的消息体长度*/
        out.writeInt(bytes.length);
        /*9. 写入内容*/
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /*魔数:387383298,转换成byte数组即为{23,23,0,2}*/
        int magic = 387383298;

        int magicNum = in.readInt();
        if (magicNum != magic) {
            log.error("消息{}反序列化失败,魔数错误", in.array());
            throw new RuntimeException("消息反序列化失败");
        }
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        byte compress = in.readByte();
        /*读取消息体长度*/
        int length = in.readInt();
        /*消息体*/
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        /*解压消息体*/
        if (compress != 0) {
            bytes = Config.getCompressAlgorithm().unCompress(bytes);
        }
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
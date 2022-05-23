package codec;

import compressor.CompressionAlgorithm;
import compressor.CompressionAlgorithmFactory;
import message.ErrorMessageBody;
import message.MessageBody;
import message.MessageHeader;
import message.MessageProtocol;
import message.MessageType;
import protocol.ProtocolConstants;
import serializer.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import serializer.SerializerAlgorithmFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author DearAhri520
 * @author DearAhri520
 * 协议解析
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接受到的 ByteBuf 消息是完整的
 * <p>
 * MessageProtocol        ByteBuf       RequestMessage    方法调用    ResponseMessage                 MessageProtocol
 * client --------------> decode(解码) -----------------> (handler) -----------------> encode(编码) ------------------> Client
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, MessageProtocol> {
    /**
     * 解码
     * 字节流 -> 消息
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        MessageProtocol messageProtocol = new MessageProtocol();
        MessageBody body;
        /*1. 读取魔数*/
        int magicNum = in.readInt();
        /*2. 读取消息版本*/
        byte version = in.readByte();
        /*3. 读取消息类型*/
        byte type = in.readByte();
        /*4. 读取消息序列号*/
        int sequenceId = in.readInt();
        /*5. 读取序列化算法ID*/
        byte serializer = in.readByte();
        /*6. 读取压缩算法ID*/
        byte compress = in.readByte();
        /*7. 读取消息体长度*/
        int bodyLen = in.readInt();
        MessageHeader header = new MessageHeader(magicNum, version, type, sequenceId, serializer, compress, bodyLen);
        messageProtocol.setHeader(header);
        /*检查magicNumber*/
        if (magicNum != ProtocolConstants.MAGIC_NUMBER) {
            log.error("Message deserialization failed , magic Number is wrong", in.array());
            header.setType(MessageType.ErrorMessage.getMessageType());
            header.setMagicNum(ProtocolConstants.MAGIC_NUMBER);
            body = new ErrorMessageBody(new RuntimeException("Magic Number is wrong"));
            messageProtocol.setBody(body);
            out.add(messageProtocol);
            return;
        }
        byte[] bytes = new byte[header.getBodyLength()];
        /*8. 读取消息体内容*/
        in.readBytes(bytes, 0, header.getBodyLength());
        /*解压消息体*/
        if (header.getCompress() != 0) {
            CompressionAlgorithm ca = CompressionAlgorithmFactory.getCompressorAlgorithm(header.getCompress());
            if (ca == null) {
                log.error("Compression algorithm {} identifier is not exist", header.getCompress());
                header.setType(MessageType.ErrorMessage.getMessageType());
                body = new ErrorMessageBody(new RuntimeException("Compression algorithm identifier is wrong"));
                messageProtocol.setBody(body);
                out.add(messageProtocol);
                return;
            }
            bytes = ca.unCompress(bytes);
        }
        /*反序列化消息体*/
        SerializerAlgorithm algorithm;
        /*消息反序列化失败*/
        if ((algorithm = SerializerAlgorithmFactory.getSerializerAlgorithm(header.getSerializer())) == null) {
            log.error("Deserialization algorithm {} identifier is not exist", header.getSerializer());
            header.setType(MessageType.ErrorMessage.getMessageType());
            body = new ErrorMessageBody(new RuntimeException("Deserialization algorithm identifier is wrong"));
            messageProtocol.setBody(body);
            out.add(messageProtocol);
            return;
        }
        /*获取具体的消息体类型*/
        Class<?> messageClass = MessageBody.getMessageClass(header.getType());
        if (messageClass == null) {
            log.error("Message type {} is not exist", header.getType());
            header.setType(MessageType.ErrorMessage.getMessageType());
            body = new ErrorMessageBody(new RuntimeException("Message type is not wrong"));
            messageProtocol.setBody(body);
            out.add(messageProtocol);
            return;
        }
        body = (MessageBody) algorithm.deserialize(messageClass, bytes);
        messageProtocol.setBody(body);
        out.add(messageProtocol);
    }

    /**
     * 编码
     * 消息 -> 字节流
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, List<Object> outList) throws IOException {
        ByteBuf out = ctx.alloc().buffer();
        /*1. 4 字节的魔数 ,转换成int即为 387383298 */
        out.writeBytes(ProtocolConstants.MAGIC_NUMBER_BYTES);
        /*2. 1 字节的消息版本*/
        out.writeByte(msg.getHeader().getVersion());
        /*3. 1字节的消息类型*/
        out.writeByte(msg.getHeader().getType());
        /*4. 4 字节的消息序列号*/
        out.writeInt(msg.getHeader().getSequenceId());
        /*5. 1 字节的序列化算法ID jdk 0 , json 1 , protostuff 2*/
        out.writeByte(msg.getHeader().getSerializer());
        /*将 MessageBody 序列化为字节数组*/
        SerializerAlgorithm serializer = SerializerAlgorithmFactory.getSerializerAlgorithm(msg.getHeader().getSerializer());
        byte[] bytes = serializer.serialize(msg.getBody());
        /*压缩MessageBody*/
        CompressionAlgorithm compressAlgorithm = CompressionAlgorithmFactory.getCompressorAlgorithm(msg.getHeader().getCompress());
        /*如果消息体达到压缩最小消息长度,则对消息体进行压缩,并获取压缩后的消息*/
        if (bytes.length >= compressAlgorithm.getMinCompressLength()) {
            bytes = compressAlgorithm.compress(bytes);
            /*6. 1 字节的压缩算法ID*/
            out.writeByte(compressAlgorithm.getIdentifier());
        } else {
            /*6. 1 字节的压缩算法ID*/
            out.writeByte(0);
        }
        /*7. 4 字节的消息体长度*/
        out.writeInt(bytes.length);
        /*8. 消息体内容*/
        out.writeBytes(bytes);
        outList.add(out);
    }
}
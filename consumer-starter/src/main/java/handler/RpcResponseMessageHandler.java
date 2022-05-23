package handler;

import message.*;
import promises.Promises;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DearAhri520
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        MessageHeader header = msg.getHeader();
        MessageBody body = msg.getBody();
        Promise<Object> promise = Promises.PROMISES.remove(header.getSequenceId());
        if (promise == null) {
            return;
        }
        byte type = header.getType();
        if (type == MessageType.PongMessage.getMessageType()) {
            log.info("接收一个心跳响应包");
        } else if (type == MessageType.ResponseMessage.getMessageType()) {
            promise.setSuccess(((ResponseMessageBody) body).getReturnValue());
        } else if (type == MessageType.ErrorMessage.getMessageType()) {
            promise.setFailure(((ErrorMessageBody) body).getException());
        } else {
            promise.setFailure(new RuntimeException("无法处理的消息类型"));
        }
    }
}

package handler;

import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import message.*;
import protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import provider.ServiceCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author DearAhri520
 * <p>
 * 仅处理 MessageProtocol 消息类
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        MessageHeader header = msg.getHeader();
        byte msgType = header.getType();

        /*心跳请求消息*/
        if (msgType == MessageType.PingMessage.getMessageType()) {
            header.setType(MessageType.PongMessage.getMessageType());
            msg.setBody(new PongMessageBody());
        }
        /*调用RPC*/
        else if (msgType == MessageType.RpcRequestMessage.getMessageType()) {
            try {
                Object returnValue = handler(msg);
                RpcResponseMessageBody body = new RpcResponseMessageBody();
                header.setType(MessageType.RpcResponseMessage.getMessageType());
                body.setReturnValue(returnValue);
            } catch (Exception e) {
                log.error("Method invoke error", e);
                header.setType(MessageType.ErrorMessage.getMessageType());
                msg.setBody(new ErrorMessageBody(new RuntimeException("Method invoke error")));
            }
        }
        /*解码过程出错*/
        else if (msgType == MessageType.ErrorMessage.getMessageType()) {
            //do nothing
        }
        /*不能处理该消息类型*/
        else {
            msg.setBody(new ErrorMessageBody(new RuntimeException("Message type could not be processed")));
            header.setType(MessageType.ErrorMessage.getMessageType());
            log.warn("Message type could be processed");
        }
        ctx.writeAndFlush(msg);
    }

    /**
     * rpc调用处理
     *
     * @param msg 消息
     * @return 调用返回结果
     */
    private Object handler(MessageProtocol msg) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        RpcRequestMessageBody body = (RpcRequestMessageBody) msg.getBody();
        Object service = ServiceCache.getService(body.getInterfaceName());
        Method method = service.getClass().getMethod(body.getMethodName(), body.getParameterTypes());
        return method.invoke(service, body.getParameterValue());
    }
}

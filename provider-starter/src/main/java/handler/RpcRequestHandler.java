package handler;

import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import message.*;
import message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import provider.ServiceCache;
import utils.MessageUtils;

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
            log.info("{}发送了心跳包请求消息", ctx.channel().remoteAddress());
            ctx.writeAndFlush(MessageUtils.newPongMessage());
            return;
        }
        /*调用RPC*/
        else if (msgType == MessageType.RequestMessage.getMessageType()) {
            log.info("{}:发送了一个rpc请求:{}", ctx.channel().remoteAddress(), msg);
            try {
                Object returnValue = handler(msg);
                ResponseMessageBody body = new ResponseMessageBody(returnValue);
                header.setType(MessageType.ResponseMessage.getMessageType());
                msg.setBody(body);
            } catch (Exception e) {
                log.error("Method invoke error", e);
                header.setType(MessageType.ErrorMessage.getMessageType());
                msg.setBody(new ErrorMessageBody(new RuntimeException("Method invoke error")));
            }
        }
        /*解码过程出错*/
        else if (msgType == MessageType.ErrorMessage.getMessageType()) {
            log.warn("解码过程出错,无法处理");
            //do nothing
        }
        /*不能处理该消息类型*/
        else {
            msg.setBody(new ErrorMessageBody(new RuntimeException("Message type could not be processed")));
            header.setType(MessageType.ErrorMessage.getMessageType());
            log.warn("Message type could be processed");
        }
        log.info("向{}响应rpc请求:{}", ctx.channel().remoteAddress(), msg);
        ctx.writeAndFlush(msg);
    }

    /**
     * rpc调用处理
     *
     * @param msg 消息
     * @return 调用返回结果
     */
    private Object handler(MessageProtocol msg) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        RequestMessageBody body = (RequestMessageBody) msg.getBody();
        Object service = ServiceCache.getService(body.getInterfaceName());
        Method method = service.getClass().getMethod(body.getMethodName(), body.getParameterTypes());
        return method.invoke(service, body.getParameterValue());
    }
}

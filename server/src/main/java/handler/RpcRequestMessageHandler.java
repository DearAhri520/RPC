package handler;

import message.RpcRequestMessage;
import message.RpcResponseMessage;
import service.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @author DearAhri520
 * @date 2022/3/28
 * <p>
 * 仅处理 RpcRequestMessage 消息类
 */
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());
        try {
            Object service = ServicesFactory.getInstance(Class.forName(msg.getInterfaceName()));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错:" + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(response);
    }
}
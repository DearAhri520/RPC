package handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author DearAhri520
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    @Override
    /**
     * 连接断开时触发该事件
     * */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接 {} 已经断开", ctx.channel());
    }

    @Override
    /**
     * 出现异常时触发该事件
     * */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*记录异常日志*/
        log.error(Arrays.toString(cause.getStackTrace()));
        /*关闭连接*/
        ctx.channel().close();
        log.info("尝试关闭连接 {}", ctx.channel());
    }
}

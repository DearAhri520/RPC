package RPC.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DearAhri520
 * @date 2022/3/22
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    @Override
    /**
     * 正常连接断开时触发该事件
     * */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} 已经正常断开", ctx.channel());
    }

    @Override
    /**
     * 异常连接断开时触发该事件
     * */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("{} 已经异常断开", ctx.channel());
    }
}

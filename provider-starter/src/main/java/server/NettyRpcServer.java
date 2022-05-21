package server;

import properties.RpcServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import protocol.ProtocolFrameDecoder;
import handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import codec.MessageCodecSharable;


/**
 * @author DearAhri520
 */
@Slf4j
@Component
public class NettyRpcServer implements RpcServer {
    @Autowired
    private RpcServerProperties properties;

    /**
     * 服务端启动
     */
    @Override
    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        /*心跳处理handler,心跳包handler,5秒内未收到channel的数据 , 触发 IdleState#READER_IDLE 事件*/
        IdleStateHandler idleStateHandler = new IdleStateHandler(25, 0, 0);
        /*心跳响应handler,该处理器可以同时作为入栈与出栈处理器,负责处理READER_IDLE事件*/
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof IdleStateEvent) {
                    /*获取事件*/
                    IdleStateEvent event = (IdleStateEvent) evt;
                    /*读空闲超过25s,则认为网络异常,关闭连接*/
                    if (event.state() == IdleState.READER_IDLE) {
                        ctx.channel().close();
                        log.debug("读空闲已经超过25秒,关闭连接");
                    }
                }
            }
        };
        /*黏包半包处理*/
        ProtocolFrameDecoder protocolFrameDecoder = new ProtocolFrameDecoder();
        /*日志处理handler*/
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        /*编解码handler*/
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        /*rpc逻辑处理handler*/
        RpcRequestHandler rpcHandler = new RpcRequestHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(idleStateHandler);
                    ch.pipeline().addLast(channelDuplexHandler);
                    ch.pipeline().addLast(protocolFrameDecoder);
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);
                    ch.pipeline().addLast(rpcHandler);
                    ch.pipeline().addLast(new QuitHandler());
                }
            });
            Channel channel = serverBootstrap.bind(properties.getPort()).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Rpc server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            log.info("Rpc server close");
        }
    }
}
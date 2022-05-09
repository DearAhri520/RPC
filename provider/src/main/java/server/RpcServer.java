package server;

import config.Config;
import config.ServerConfig;
import curator.CuratorToServer;
import protocol.MessageCodecSharable;
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
import serializer.SerializerAlgorithm;
import spi.FactoriesLoader;

import java.util.HashMap;
import java.util.List;

/**
 * @author DearAhri520
 */
@Slf4j
public class RpcServer {
    private ServerConfig config = new ServerConfig();

    /**
     * 服务端启动
     */
    public void start() {
        /*连接zookeeper*/
        CuratorToServer curator = new CuratorToServer();
        try {
            curator.connect();
            /*获取配置中心的服务器配置*/
            HashMap<String, String> configMap = curator.getConfig();
            /*向消费中心传递为消费者提供服务的地址*/
            curator.addService(config.getSelfIPAddress() + ":" + config.getSelfPort());
            /*初始化配置*/
            config.setConfig(configMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable(config);
        RpcRequestMessageHandler rpcHandler = new RpcRequestMessageHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    /*心跳包handler,5秒内未收到channel的数据 , 触发 IdleState#READER_IDLE 事件*/
                    ch.pipeline().addLast(new IdleStateHandler(25, 0, 0));
                    /*添加双向处理器ChannelDuplexHandler,该处理器可以同时作为入栈与出栈处理器,负责处理READER_IDLE事件*/
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
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
                    });
                    /*处理黏包半包*/
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);
                    ch.pipeline().addLast(rpcHandler);
                    ch.pipeline().addLast(new QuitHandler());
                }
            });
            Channel channel = serverBootstrap.bind(config.getSelfPort()).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
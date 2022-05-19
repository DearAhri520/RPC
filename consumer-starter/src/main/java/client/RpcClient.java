package client;

import autoconfigure.RpcClientProperties;
import handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import message.PingMessageBody;
import message.RpcRequestMessageBody;
import promises.Promises;
import protocol.ProtocolFrameDecoder;
import protocol.SequenceIdGenerator;
import servers.ServerList;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author DearAhri520
 */
@Slf4j
public class RpcClient {
    /**
     * 客户端配置
     */
    private RpcClientProperties config = new RpcClientProperties();

    /**
     * 可用服务列表
     */
    private ServerList serverList;

    public RpcClient() {
        this.serverList = new ServerList(config);
    }

    /**
     * 返回代理类
     *
     * @param <T> 代理接口
     * @return 代理对象
     */
    public <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{
                serviceClass
        };
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextInt();
            /*1.将方法调用转换为消息对象*/
            RpcRequestMessageBody message = new RpcRequestMessageBody(
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            /*2. 根据接口名获取服务器地址*/
            String[] connectString = serverList.getProvider(serviceClass.getName(), message).split(":");
            /*获取channel*/
            Channel channel = getChannel(connectString[0], connectString[1]);
            log.info("连接服务器: IP:" + connectString[0] + " ,端口:" + connectString[1]);
            /*3.准备一个promise对象来接收结果,指定异步接收结果的线程*/
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            /*4.设置消息序号*/
            /*message.setSequenceId(sequenceId);*/
            /*5.发送消息对象*/
            channel.writeAndFlush(message);
            Promises.PROMISES.put(sequenceId, promise);
            /*6.等待promise结果*/
            try {
                promise.await(10, TimeUnit.SECONDS);
                /*正常调用*/
                if (promise.isSuccess()) {
                    return promise.getNow();
                }
                /*异常调用*/
                else {
                    throw new RuntimeException(promise.cause());
                }
            } finally {
                channel.close();
                log.info("连接已关闭");
            }
        });
        return (T) o;
    }

    private Channel getChannel(String ipAddress, String port) {
        return initChannel(ipAddress, port);
    }

    /**
     * 初始化channel,短连接
     */
    private Channel initChannel(String ipAddress, String port) {
        Channel channel = null;
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        /*ServerMessageCodecSharable messageCodecSharable = new ServerMessageCodecSharable(config);*/
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
        IdleStateHandler idleStateHandler = new IdleStateHandler(0, 10, 0);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                /*ch.pipeline().addLast(messageCodecSharable);*/
                ch.pipeline().addLast(rpcHandler);
                /*3秒内未向服务器发送数据 , 触发 IdleState#QRITER_IDLE 事件*/
                ch.pipeline().addLast(idleStateHandler);
                /*添加双向处理器ChannelDuplexHandler, 该处理器可以同时作为入栈与出栈处理器, 负责处理READER_IDLE事件*/
                ch.pipeline().addLast(new ChannelDuplexHandler() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        /*获取事件*/
                        IdleStateEvent event = (IdleStateEvent) evt;
                        if (event.state() == IdleState.WRITER_IDLE) {
                            log.debug("10s内未写入数据,发送心跳包");
                            ctx.writeAndFlush(new PingMessageBody());
                        }
                    }
                });
            }
        });
        try {
            channel = bootstrap.connect(ipAddress, Integer.parseInt(port)).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            log.error("client error", e);
        }
        return channel;
    }
}
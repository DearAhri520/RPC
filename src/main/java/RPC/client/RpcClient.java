package RPC.client;

import RPC.config.Config;
import RPC.message.PingMessage;
import RPC.message.RpcRequestMessage;
import RPC.protocol.MessageCodecSharable;
import RPC.protocol.ProtocolFrameDecoder;
import RPC.protocol.SequenceIdGenerator;
import RPC.server.handler.RpcResponseMessageHandler;
import RPC.service.HelloService;
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

import java.lang.reflect.Proxy;

/**
 * @author DearAhri520
 * @date 2022/3/20
 */
@Slf4j
public class RpcClient {
    private static volatile Channel channel = null;

    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);
        System.out.println(service.sayHello("你好"));
        System.out.println(service.sayHello("我不好"));
    }

    /**
     * 返回代理类
     *
     * @param <T> 代理接口
     * @return 代理对象
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{
                serviceClass
        };
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextInt();
            /*1.将方法调用转换为消息对象*/
            RpcRequestMessage message = new RpcRequestMessage(
                    SequenceIdGenerator.nextInt(),
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            /*2.准备一个promise对象来接收结果,指定异步接收结果的线程*/
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            /*3.设置消息序号*/
            message.setSequenceId(sequenceId);
            /*4.发送消息对象*/
            getChannel().writeAndFlush(message);
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);
            /*等待promise结果*/
            promise.await();
            /*正常调用*/
            if (promise.isSuccess()) {
                return promise.getNow();
            }
            /*异常调用*/
            else {
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    /**
     * 初始化channel
     */
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
        IdleStateHandler idleStateHandler = new IdleStateHandler(0, 3, 0);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageCodecSharable);
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
                            log.debug("3s内未写入数据,发送心跳包");
                            ctx.writeAndFlush(new PingMessage());
                        }
                    }
                });
            }
        });
        try {
            channel = bootstrap.connect(Config.getServerIPAddress(), Config.getServerPort()).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            log.error("client error", e);
        }
    }

    public static Channel getChannel() {
        if (channel == null) {
            synchronized (LOCK) {
                if (channel == null) {
                    initChannel();
                }
            }
        }
        return channel;
    }
}
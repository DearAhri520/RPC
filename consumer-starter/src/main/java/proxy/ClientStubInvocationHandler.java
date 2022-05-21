package proxy;

import autoconfigure.RpcClientProperties;
import codec.MessageCodecSharable;
import compressor.CompressionAlgorithmFactory;
import discovery.ServiceDiscovery;
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
import lombok.extern.slf4j.Slf4j;
import message.MessageBody;
import message.MessageHeader;
import message.MessageProtocol;
import message.RpcRequestMessageBody;
import protocol.MessageType;
import protocol.ProtocolConstants;
import protocol.ProtocolFrameDecoder;
import protocol.SequenceIdGenerator;
import serializer.SerializerAlgorithmFactory;
import servers.ServerChannelCache;
import servers.ServerInfo;
import serviceinfo.ServiceInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author DearAhri520
 */
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {
    private Class<?> clazz;

    private RpcClientProperties properties;

    private ServiceDiscovery serviceDiscovery;

    public ClientStubInvocationHandler(Class<?> clazz, RpcClientProperties properties, ServiceDiscovery serviceDiscovery) {
        super();
        this.clazz = clazz;
        this.properties = properties;
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        ServiceInfo serviceInfo = serviceDiscovery.discover(clazz.getName());


        MessageProtocol messageProtocol = new MessageProtocol();
        /*消息体长度将在编解码中设定*/
        MessageHeader header = new MessageHeader(
                ProtocolConstants.MAGIC_NUMBER,
                ProtocolConstants.VERSION,
                MessageType.RpcRequestMessage.getMessageType(),
                SequenceIdGenerator.nextInt(),
                SerializerAlgorithmFactory.getSerializerAlgorithm(properties.getSerializer()).getIdentifier(),
                CompressionAlgorithmFactory.getCompressorAlgorithm(properties.getCompressor()).getIdentifier(),
                0
        );
        MessageBody requestBody = new RpcRequestMessageBody(
                clazz.getName(), method.getName(), method.getParameterTypes(), objects, method.getReturnType()
        );
        MessageProtocol protocol = new MessageProtocol();
        protocol.setHeader(header);
        protocol.setBody(requestBody);

    }

    private Channel getChannel(ServerInfo serverInfo) {
        if (ServerChannelCache.containServerChannel(serverInfo)) {
            return ServerChannelCache.getServerChannel(serverInfo);
        }
        return initChannel(serverInfo);
    }

    /**
     * 初始化channel
     */
    private Channel initChannel(ServerInfo serverInfo) {
        Channel channel = null;
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
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
                            log.debug("10s内未写入数据,发送心跳包");
                            ctx.writeAndFlush(new PingMessage());
                        }
                    }
                });
            }
        });
        try {
            channel = bootstrap.connect(serverInfo.getIpAddress(), serverInfo.getPort()).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            log.error("client error", e);
        }
        return channel;
    }
}

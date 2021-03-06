package proxy;

import properties.RpcClientProperties;
import codec.MessageCodecSharable;
import compressor.CompressionAlgorithmFactory;
import discovery.ServiceDiscover;
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
import loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import message.MessageBody;
import message.MessageHeader;
import message.MessageProtocol;
import message.RequestMessageBody;
import org.springframework.util.CollectionUtils;
import promises.Promises;
import message.MessageType;
import protocol.ProtocolConstants;
import protocol.ProtocolFrameDecoder;
import protocol.SequenceIdGenerator;
import serializer.SerializerAlgorithmFactory;
import servers.ServerChannelCache;
import serviceinfo.ServiceInfo;
import utils.MessageUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author DearAhri520
 */
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {
    private Class<?> clazz;

    private RpcClientProperties properties;

    private ServiceDiscover serviceDiscover;

    private LoadBalance loadBalance;

    public ClientStubInvocationHandler(Class<?> clazz, RpcClientProperties properties, ServiceDiscover serviceDiscover, LoadBalance loadBalance) {
        this.clazz = clazz;
        this.properties = properties;
        this.serviceDiscover = serviceDiscover;
        this.loadBalance = loadBalance;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        /*???????????????????????????????????????*/
        MessageHeader header = new MessageHeader(
                ProtocolConstants.MAGIC_NUMBER,
                ProtocolConstants.VERSION,
                MessageType.RequestMessage.getMessageType(),
                SequenceIdGenerator.nextInt(),
                SerializerAlgorithmFactory.getSerializerAlgorithm(properties.getSerializer()).getIdentifier(),
                CompressionAlgorithmFactory.getCompressorAlgorithm(properties.getCompressor()).getIdentifier(),
                0
        );
        MessageBody requestBody = new RequestMessageBody(
                clazz.getName(), method.getName(), method.getParameterTypes(), objects, method.getReturnType()
        );
        List<ServiceInfo> serviceInfos = serviceDiscover.getAllServices(clazz.getName());
        if (CollectionUtils.isEmpty(serviceInfos)) {
            log.info("?????????????????????");
            throw new RuntimeException("?????????????????????");
        }
        ServiceInfo serviceInfo = loadBalance.getServer(serviceInfos, requestBody);
        MessageProtocol protocol = new MessageProtocol();
        protocol.setHeader(header);
        protocol.setBody(requestBody);
        Channel channel = getChannel(serviceInfo);
        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        Promises.PROMISES.put(header.getSequenceId(), promise);
        channel.writeAndFlush(protocol);
        log.info("??????rpc????????????:{}", protocol);
        promise.await(10, TimeUnit.SECONDS);
        /*????????????*/
        if (promise.isSuccess()) {
            return promise.getNow();
        }
        /*????????????*/
        else {
            throw new RuntimeException(promise.cause());
        }
    }

    private Channel getChannel(ServiceInfo serviceInfo) {
        if (ServerChannelCache.containServerChannel(serviceInfo)) {
            return ServerChannelCache.getServerChannel(serviceInfo);
        }
        Channel channel = initChannel(serviceInfo);
        ServerChannelCache.addServerChannel(serviceInfo, channel);
        return channel;
    }

    /**
     * ?????????channel
     */
    private Channel initChannel(ServiceInfo serviceInfo) {
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
                /*10????????????????????????????????? , ?????? IdleState#QRITER_IDLE ??????*/
                ch.pipeline().addLast(idleStateHandler);
                /*?????????????????????ChannelDuplexHandler, ??????????????????????????????????????????????????????, ????????????READER_IDLE??????*/
                ch.pipeline().addLast(new ChannelDuplexHandler() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        /*????????????*/
                        IdleStateEvent event = (IdleStateEvent) evt;
                        if (event.state() == IdleState.WRITER_IDLE) {
                            log.debug("10s??????????????????,???????????????");
                            ctx.writeAndFlush(MessageUtils.newPingMessage());
                        }
                    }
                });
            }
        });
        try {
            channel = bootstrap.connect(serviceInfo.getIpAddress(), serviceInfo.getPort()).sync().addListener(
                    future -> log.info("??????????????? ip??????:{}, ??????:{}", serviceInfo.getIpAddress(), serviceInfo.getPort())
            ).channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
                log.info("????????????????????? ip??????:{}, ??????:{}", serviceInfo.getIpAddress(), serviceInfo.getPort());
            });
        } catch (Exception e) {
            log.error("???????????????:", e);
        }
        return channel;
    }
}

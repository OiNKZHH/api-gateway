package cn.oink.gateway.session;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * 网关会话服务器
 *
 * @author OiNK
 * @date 2024/07/24
 */
public class SessionServer implements Callable<Channel> {

    private final Logger logger = LoggerFactory.getLogger(SessionServer.class);

    /**
     * EventLoopGroup 分别启动的是你的连接等待和数据处理
     * 之后这里的 childHandler 添加的就是会话的初始信息。
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Channel call() throws Exception {
        ChannelFuture channelFuture = null;

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new SessionChannelInitializer());

            channelFuture = serverBootstrap.bind(new InetSocketAddress(8899)).syncUninterruptibly();
            this.channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error("socket server start error.", e);
        }finally {
            if (null != channelFuture && channelFuture.isSuccess()){
                logger.info("socket server start done.");
            }else {
                logger.error("socket server start error.");
            }
        }

        return channel;
    }
}

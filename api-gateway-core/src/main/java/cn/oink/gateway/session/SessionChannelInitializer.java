package cn.oink.gateway.session;

import cn.oink.gateway.session.handlers.SessionServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

/**
 * 会话通道初始化器
 * Netty 的通信会为每个连接上来的用户建立一条 Channle 管道（对应的 ChannelID 唯一），
 * 并在管道中插入一道道板子，这些板子可以是 编码器、解码器、流量整形、SSL、自定义服务处理等各类模块。
 * 通过这样的方式，让我们可以扩展各类功能。对应的也就是接口 ChannelInitializer 的实现类所完成的事情。
 * @author OiNK
 * @date 2024/07/24
 */
public class SessionChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        //HttpRequestDecoder、HttpResponseEncoder 是 Netty 本身提供的HTTP编解器，这部分涉及到网络通信中的通信协议和半包粘包处理
        pipeline.addLast(new HttpRequestDecoder());

        pipeline.addLast(new HttpRequestEncoder());

        //HttpObjectAggregator 用于处理除了 GET 请求外的 POST 请求时候的对象信息，否则只有上面的信息，是拿不到 POST 请求的。
        // *这就很像不断的在管道中添加板子，不同的板子处理不同的功能*
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 ));

        //SessionServerHandler 是我们自己实现的会话处理，用于拿到HTTP网络请求后，处理我们自己需要的业务逻辑。
        pipeline.addLast(new SessionServerHandler());
    }
}

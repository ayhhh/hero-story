package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.util.RedisUtil;

public class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
    public static void main(String[] args) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        CmdHandlerFactory.init(); // 初始化工厂
        GameMsgRecognizer.init();
        MySqlSessionFactory.init();
        RedisUtil.init();
        MqProducer.init(); // 消息队列初始化

        // 拉客的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 干活的
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup);
            b.channel(NioServerSocketChannel.class); // 服务器信道的处理方式
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                // 每一个客户端连上来，都会初始化一套下面的内容，因此GameMsgHandler类中的ChannelGroup必须是静态的
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new HttpServerCodec(),  // http服务器编解码器
                            new HttpObjectAggregator(65535), // 内容长度限制
                            // websocket协议处理器，在这里处理握手、ping, pong等
                            new WebSocketServerProtocolHandler("/websocket"),
                            new GameMsgDecoder(), // 自定义消息解码器
                            new GameMsgEncoder(), // 自定义消息编码器
                            new GameMsgHandler() // 自定义的消息处理器
                    );
                }
            });

            b.option(ChannelOption.SO_BACKLOG,128);
            b.childOption(ChannelOption.SO_KEEPALIVE,true);

            // 绑定端口号
            ChannelFuture f = b.bind(12345).sync(); // 同步等待

            if(f.isSuccess()){
                LOGGER.info("游戏服务器启动成功");
            }

            // 等待服务器信道关闭
            // 也就是不要立即退出应用程序，让应用程序可以一直提供服务
            f.channel().closeFuture().sync();

        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

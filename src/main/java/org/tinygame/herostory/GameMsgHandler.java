package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.*;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(ctx == null || msg == null){
            return ;
        }
        MainMsgProcessor.getInstance().process(ctx,msg);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null){
            return ;
        }
        try {
            super.channelActive(ctx);
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        if(ctx == null){
            return ;
        }
        try {
            super.handlerRemoved(ctx);

            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if(userId == null){
                return ;
            }
            UserManager.removeByUserId(userId);   // 移除用户Id
            Broadcaster.removeChannel(ctx.channel()); // 移除channel

            // 通知其他用户
            GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
            builder.setQuitUserId(userId);
            GameMsgProtocol.UserQuitResult newResult = builder.build();
            Broadcaster.broadcast(newResult);

        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(),exception);
        }
    }

}

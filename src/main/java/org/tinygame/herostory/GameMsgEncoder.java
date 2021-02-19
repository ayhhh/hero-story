package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 游戏消息的编码器
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(ctx == null || msg == null){
            return ;
        }

        try {
            if(!(msg instanceof GeneratedMessageV3)){
                super.write(ctx, msg, promise);
                return ;
            }

            // 消息编码
            int msgCode = GameMsgRecognizer.getMessageCodeByClazz(msg.getClass());
            if(msgCode == -1){
                LOGGER.error("无法识别的消息类型, msgClazz={}", msg.getClass().getSimpleName());
                super.write(ctx,msg,promise);
                return ;
            }

            // 消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short) msgBody.length); // 消息的长度
            byteBuf.writeShort((short) msgCode); // 消息编号
            byteBuf.writeBytes(msgBody);  // 消息体
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);

            super.write(ctx, outputFrame, promise);
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }
}

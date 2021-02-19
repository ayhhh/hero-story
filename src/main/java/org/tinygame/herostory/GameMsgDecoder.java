package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 自定义的消息解码器
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(ctx == null || msg == null){
            return ;
        }

        // 必须是字节数组才能往下走，否则没有必须继续解析
        // 游戏考虑的是吞吐量，因此不适合使用string，字节数组更合适
        if(!(msg instanceof BinaryWebSocketFrame)){
            return ;
        }
        try {
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content(); // 拿到了字节缓冲区


            // 这个length没有使用，原因在于netty的基础代码已经在底层把粘包的问题处理了
            // 即
            short length = byteBuf.readShort(); //前两个字节是消息的长度
            int msgCode = byteBuf.readShort(); // 读取消息编号（这个编号即为操作的类型）

            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody); // 读入字节数组

            Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);

            Message cmd = msgBuilder.build();
            if (cmd != null) {
                ctx.fireChannelRead(cmd); // 扔给pipeline的下一步
            }
        } catch (Exception exception){
            // 记录错误日志
            LOGGER.error(exception.getMessage(),exception);
        }



    }
}

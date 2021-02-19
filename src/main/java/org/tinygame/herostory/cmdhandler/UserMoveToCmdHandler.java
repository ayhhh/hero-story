package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements CmdHandler<GameMsgProtocol.UserMoveToCmd>{
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd){
        if(ctx == null || cmd == null){
            return ;
        }
        // 用户移动
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();

        // 如何拿userId
        // 去session拿
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(userId == null){
            return ;
        }
        User user = UserManager.getByUserId(userId);
        if(user == null) {
            return ;
        }

        long nowTime = System.currentTimeMillis();

        user.getMoveState().setFromPosX(cmd.getMoveFromPosX());
        user.getMoveState().setFromPosY(cmd.getMoveFromPosY());
        user.getMoveState().setToPosX(cmd.getMoveToPosX());
        user.getMoveState().setToPosY(cmd.getMoveToPosY());
        user.getMoveState().setStartTime(nowTime);

        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        resultBuilder.setMoveStartTime(nowTime);

        // 广播
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}

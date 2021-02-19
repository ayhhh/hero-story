package org.tinygame.herostory.cmdhandler;

import com.sun.media.jfxmedia.control.MediaPlayerOverlay;
import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class WhoElseIsHereCmdHandler implements CmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        for(User currUser: UserManager.listUser()){
            if(null == currUser){
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();

            userInfoBuilder.setUserId(currUser.getUserId());
            userInfoBuilder.setHeroAvatar(currUser.getHeroAvatar());
            userInfoBuilder.setUserName(currUser.getUserName());

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            moveStateBuilder.setFromPosX(currUser.getMoveState().getFromPosX());
            moveStateBuilder.setFromPosY(currUser.getMoveState().getFromPosY());
            moveStateBuilder.setToPosX(currUser.getMoveState().getToPosX());
            moveStateBuilder.setToPosY(currUser.getMoveState().getToPosY());
            moveStateBuilder.setStartTime(currUser.getMoveState().getStartTime());

            userInfoBuilder.setMoveState(moveStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }
        GameMsgProtocol.WhoElseIsHereResult result = resultBuilder.build();
        ctx.writeAndFlush(result);
    }
}

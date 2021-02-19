package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.function.Function;

public class UserLoginCmdHandler implements CmdHandler<GameMsgProtocol.UserLoginCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if(ctx == null || cmd == null){
            return ;
        }
        String userName = cmd.getUserName();
        String password = cmd.getPassword();
        if(userName == null || password == null){
            return ;
        }


        LoginService.getInstance().userLogin(userName, password,(userEntity)->{
            GameMsgProtocol.UserLoginResult.Builder builder = GameMsgProtocol.UserLoginResult.newBuilder();

            if(userEntity == null){
                builder.setUserId(-1);
                builder.setUserName("");
                builder.setHeroAvatar("");
            } else {
                User newUser = new User(userEntity.getUserId(), userEntity.getHeroAvatar());
                newUser.setCurrHp(100); //设置血量
                newUser.setUserName(userEntity.getUserName());
                UserManager.addUser(newUser);
                // 将用户Id保存至 session
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(userEntity.getUserId());
                builder.setUserId(userEntity.getUserId());
                builder.setUserName(userEntity.getUserName());
                builder.setHeroAvatar(userEntity.getHeroAvatar());
            }

            GameMsgProtocol.UserLoginResult result = builder.build();

            ctx.writeAndFlush(result);

            return null;
        });


    }
}

package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.ibatis.session.SqlSession;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class SelectHeroCmdHandler implements CmdHandler<GameMsgProtocol.SelectHeroCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.SelectHeroCmd cmd) {
        if(ctx == null || cmd == null){
            return ;
        }
        String heroAvatar = cmd.getHeroAvatar();
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        // LoginService.getInstance().setHeroAvatar(userId,heroAvatar);

        GameMsgProtocol.SelectHeroResult.Builder builder = GameMsgProtocol.SelectHeroResult.newBuilder();
        builder.setHeroAvatar(heroAvatar);
        GameMsgProtocol.SelectHeroResult result = builder.build();
        ctx.writeAndFlush(result);

    }
}

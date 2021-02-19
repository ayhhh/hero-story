package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.rank.RankItem;
import org.tinygame.herostory.rank.RankService;

import java.util.Collections;

public class GetRankCmdHandler implements CmdHandler<GameMsgProtocol.GetRankCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if(ctx == null || cmd == null){
            return;
        }

        RankService.getInstance().getRank((rankItemList)->{
            if(rankItemList == null){
                rankItemList = Collections.emptyList();
            }
            GameMsgProtocol.GetRankResult.Builder builder = GameMsgProtocol.GetRankResult.newBuilder();

            for(RankItem rankItem: rankItemList){
                if(rankItem == null){
                    continue;
                }
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();

                rankItemBuilder.setRankId(rankItem.getRankId());
                rankItemBuilder.setUserId(rankItem.getUserId());
                rankItemBuilder.setUserName(rankItem.getUserName());
                rankItemBuilder.setHeroAvatar(rankItem.getHeroAvatar());
                rankItemBuilder.setWin(rankItem.getWin());

                builder.addRankItem(rankItemBuilder);
            }
            GameMsgProtocol.GetRankResult result = builder.build();
            ctx.writeAndFlush(result);

            return null;
        });
    }
}

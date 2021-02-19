package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements CmdHandler<GameMsgProtocol.UserAttkCmd>{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd userAttkCmd) {
        if(ctx == null || userAttkCmd == null){
            return ;
        }

        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(attkUserId == null){
            return ;
        }

        // 获取目标用户Id和用户
        int targetUserId = userAttkCmd.getTargetUserId();


        User targetUser = UserManager.getByUserId(targetUserId);

        // 攻击了空气
        if(targetUser == null){
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        // todo 根据角色的不同设置暴击、防御、之类的
        final int dmgPoint = 10;
        int hp = targetUser.getCurrHp() - dmgPoint;

        targetUser.setCurrHp(hp);

        // 广播攻击结果
        broadcastAttkResult(attkUserId,targetUserId);

        // 减血结果
        broadcastSubtractHpResult(targetUserId,dmgPoint);

        if(hp<=0){
            // todo 执行死亡逻辑
            broadcastDieResult(targetUserId);

            // 发送消息给MQ
            VictorMsg msg = new VictorMsg();
            msg.setWinnerId(attkUserId);
            msg.setLoserId(targetUserId);
            MqProducer.senMsg("herostory_victory",msg);
        }


    }

    /**
     * 广播攻击结果
     * @param attkUserId
     * @param targetUserId
     */
    private static void broadcastAttkResult(int attkUserId, int targetUserId){
        if(attkUserId<=0){
            return ;
        }
        GameMsgProtocol.UserAttkResult.Builder attkResultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        attkResultBuilder.setAttkUserId(attkUserId);
        attkResultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserAttkResult attkResult = attkResultBuilder.build();

        // 广播出去
        Broadcaster.broadcast(attkResult);
    }


    /**
     * 广播减血结果
     * @param targetUserId
     * @param subtractHp
     */
    private static void broadcastSubtractHpResult(int targetUserId, int subtractHp){
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }
        GameMsgProtocol.UserSubtractHpResult.Builder subtractHpResultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        subtractHpResultBuilder.setTargetUserId(targetUserId);
        subtractHpResultBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult subtractHpResult = subtractHpResultBuilder.build();
        Broadcaster.broadcast(subtractHpResult);
    }

    private static void broadcastDieResult(int targetUserId){
        if(targetUserId <= 0){
            return ;
        }
        GameMsgProtocol.UserDieResult.Builder builder = GameMsgProtocol.UserDieResult.newBuilder();
        builder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult result = builder.build();
        Broadcaster.broadcast(result);
    }
}

package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播员
 */

public final class Broadcaster {
    /**
     * 信道组，注意这里一定是static
     * 否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化构造函数
     */
    private Broadcaster(){
    }

    /**
     * 添加信道
     * @param channel
     */
    public static void addChannel(Channel channel){
        if(channel != null){
            _channelGroup.add(channel);
        }
    }

    /**
     * 移除信道
     * @param channel
     */
    public static void removeChannel(Channel channel){
        if(channel != null){
            _channelGroup.remove(channel);
        }
    }

    public static void broadcast(Object msg){
        if(msg != null){
            _channelGroup.writeAndFlush(msg);
        }
    }
}

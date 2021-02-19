package org.tinygame.herostory;

import org.tinygame.herostory.mq.MqConsumer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * RANK进程，可以单独运行
 */
public class RankApp {
    public static void main(String[] args) {
        RedisUtil.init();
        MqConsumer.init(); // 初始化消费者
    }
}

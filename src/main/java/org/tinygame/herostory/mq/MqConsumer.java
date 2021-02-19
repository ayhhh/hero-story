package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.rank.RankService;

import java.util.List;

/**
 * 消息队列消费者
 */
public final class MqConsumer {

    private static final Logger LOGGER  = LoggerFactory.getLogger(MQConsumer.class);

    private MqConsumer(){

    }

    public static void init(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("10.0.1.10:9876");

        try {
            consumer.subscribe("herostory_victor", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext ctx) {
                    for(MessageExt msg: list){
                        VictorMsg victorMsg = JSONObject.parseObject(msg.getBody(), VictorMsg.class);
                        LOGGER.info("从消息队列中收到消息, winderId = {}, loserId = {}.",
                                victorMsg.getWinnerId(),
                                victorMsg.getLoserId());
                        RankService.getInstance().refreshRank(victorMsg.getWinnerId(),victorMsg.getLoserId());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
            LOGGER.info("消息队列（消费者）连接成功");
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }
}

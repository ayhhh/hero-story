package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息队列生产者
 */
public final class MqProducer {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);

    /**
     * 消息队列生产者
     */
    private static DefaultMQProducer _producer = null;

    private MqProducer(){

    }

    /**
     * 初始化
     */
    public static void init(){
        try{
            DefaultMQProducer producer = new DefaultMQProducer("herostory");
            producer.setNamesrvAddr("10.0.1.10:9876");
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;
            LOGGER.info("消息队列（生产者）连接成功！");
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }

    /**
     * 发送消息
     * @param topic 主题
     * @param msg   消息对象
     */
    public static void senMsg(String topic, Object msg){
        if(topic == null || msg == null){
            return ;
        }
        try {
            Message newMsg = new Message();
            newMsg.setTopic(topic);
            newMsg.setBody(JSONObject.toJSONBytes(msg));
            _producer.send(newMsg);
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }
}

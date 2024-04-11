package com.ling.base.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * 发送单向消息
 */
public class OnewayProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message msg = new Message("OnewayTopic", "OnewayTag", "Oneway Message".getBytes());
        msg.setKeys("OnewayKey" + 11);
        // 发送单向消息
        producer.sendOneway(msg);
        producer.shutdown();
    }
}

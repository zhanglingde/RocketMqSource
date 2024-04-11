package com.ling.delay;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消费延时消息
 */
// @Slf4j
public class DelayConsumer {

    private static final Logger log = LoggerFactory.getLogger(DelayConsumer.class);

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.subscribe("DelayTopic", "*");

        // 设置回调函数处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    log.info("消息 id:{},延时时间：{},消息内容：{}", msg.getMsgId(), System.currentTimeMillis() - msg.getStoreTimestamp(), new String(msg.getBody()));
                    System.out.println(String.format("消息 id:%s,延时时间：%s,消息内容：%s", msg.getMsgId(), System.currentTimeMillis() - msg.getStoreTimestamp(), new String(msg.getBody())));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("消费者启动...");
    }
}

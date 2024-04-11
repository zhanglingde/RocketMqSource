package com.ling.base.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 负载均衡模式 消费消息
 */
// @Slf4j
public class Consumer {

    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    public static void main(String[] args) throws Exception {
        // 实例化消费者 Consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        // 设置 NameServer 的地址（可以订阅多个）
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 订阅一个或多个 Topic，以及 Tag 来过滤需要消费的消息
        consumer.subscribe("SyncTopic", "*");
        consumer.subscribe("AsyncTopic", "*");
        consumer.subscribe("OnewayTopic", "*");
        consumer.subscribe("normal", "*");
        // 负载均衡模式 消费消息
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 广播模式
        // consumer.setMessageModel(MessageModel.BROADCASTING);
        // 设置消费超时时间
        consumer.setConsumeTimeout(1500);
        // 注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : msgs) {
                    log.info("topic：{},tag：{},key：{},body:{}", msg.getTopic(), msg.getTags(), msg.getKeys(), new String(msg.getBody()));
                    System.out.println(String.format("topic：%s,tag：%s,key：%s,body:%s", msg.getTopic(), msg.getTags(), msg.getKeys(), new String(msg.getBody())));
                }
                // System.out.println("线程：" + Thread.currentThread().getName() + "，消息：" + msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("消费者启动...");
    }
}

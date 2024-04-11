package com.ling.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 顺序消息发送
 */
public class OrderProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        String[] tags = new String[]{"TagA", "TagB", "TagC"};

        List<OrderStep> orderSteps = OrderStep.buildOrderStep();

        for (int i = 0; i < orderSteps.size(); i++) {
            String body = "Hello RocketMQ " + orderSteps.get(i);
            Message msg = new Message("OrderTopic", tags[i % tags.length], "KEY" + i, body.getBytes());

            /**
             * 将同一个订单 的业务处理 发送到同一个队列中
             * 参数一：消息对象
             * 参数二：消息队列选择器
             * 参数三：选择队列的业务标识（订单Id）
             */
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                /**
                 * @param queues Broker 中队列集合
                 * @param message 消息对象
                 * @param arg 业务标识的参数
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> queues, Message message, Object arg) {
                    // 根据订单 id 选择发送的 queue（保证同一个订单的详细发送到同一个队列中）
                    Long id = (Long) arg;
                    long index = id % queues.size();
                    return queues.get((int) index);

                }
            }, orderSteps.get(i).getOrderId());

            System.out.println(result);
        }
        producer.shutdown();
    }
}

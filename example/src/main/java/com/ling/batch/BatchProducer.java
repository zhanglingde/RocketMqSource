package com.ling.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送批量消息
 */
public class BatchProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();

        List<Message> msgs = new ArrayList<>();
        msgs.add(new Message("BatchTopic", "BatchTag", "Hello World 0".getBytes()));
        msgs.add(new Message("BatchTopic", "BatchTag", "Hello World 1".getBytes()));
        msgs.add(new Message("BatchTopic", "BatchTag", "Hello World 2".getBytes()));

        // 发送批量消息
        SendResult result = producer.send(msgs);
        System.out.println(result);

        //TimeUnit.SECONDS.sleep(1);
        producer.shutdown();

    }
}

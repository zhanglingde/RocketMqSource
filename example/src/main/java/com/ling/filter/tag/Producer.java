package com.ling.filter.tag;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author zhangling 2021/6/3 14:27
 */
public class Producer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 3; i++) {
            Message msg = new Message("FilterTagTopic", "Tag1", ("Hello World" + i).getBytes());
            SendResult result = producer.send(msg);

            System.out.println(result);
            
        }
        producer.shutdown();
    }
}

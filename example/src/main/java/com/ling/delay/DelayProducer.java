package com.ling.delay;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送延时消息
 * 延时级别 {@link MessageSt}
 */
public class DelayProducer {

    private static final Logger log = LoggerFactory.getLogger(DelayProducer.class);
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message msg = new Message("DelayTopic", ("Hello Scheduled Message").getBytes());
        // 设置延时等级3,这个消息将在10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
        // private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
        msg.setDelayTimeLevel(3);

        SendResult result = producer.send(msg);
        log.info("result:{}", result);
        //TimeUnit.SECONDS.sleep(1);
        producer.shutdown();
    }
}

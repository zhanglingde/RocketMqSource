package com.ling.base.producer;

import com.alibaba.fastjson.JSON;
import com.ling.User;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * 发送异步消息
 */
public class AsyncProducer {
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // 实例化消息生产者 Producer
        DefaultMQProducer producer = new DefaultMQProducer("GID_SRM_account-receivable-invoice-local");
        // 设置 NameServer 地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        producer.setRetryTimesWhenSendFailed(0);

        int messageCount = 1;
        final CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        User user = new User();
        user.setId(1);
        user.setName("zhangling");
        Message msg = new Message("AsyncTopic", "AsyncTag", JSON.toJSONString(user).getBytes());
        msg.setKeys("async_message_key" + 100);

        // SendCallback 接收异步返回结果的回调，该方式send方法为异步
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult);
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getCause().getMessage());
            }
        });
        // 等待5s后释放阻塞
        countDownLatch.await(5, TimeUnit.SECONDS);
        // 主线程关闭 Producer 实例
        producer.shutdown();
    }
}

package com.ling.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 * 事务消息
 * 发送三个消息，一个消息提交，一个回滚，一个回查后提交
 * 最后共有两个消息会被消费：TagA 和 TagC 的
 */
public class Producer {
    public static void main(String[] args) throws Exception{
        TransactionMQProducer producer = new TransactionMQProducer("group5");
        producer.setNamesrvAddr("localhost:9876");

        // 添加事务监听器
        producer.setTransactionListener(new TransactionListener() {

            /**
             * 该方法中执行本地事务
             * @param msg
             * @param arg
             * @return
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                if (StringUtils.equals("TAGA", msg.getTags())) {
                    System.out.println("taga 本地事务");
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StringUtils.equals("TAGB", msg.getTags())) {
                    System.out.println("tagb 本地事务");
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else if (StringUtils.equals("TAGC", msg.getTags())) {
                    System.out.println("tagc 本地事务");
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.UNKNOW;
            }

            /**
             * MQ 进行消息事务状态回查
             * @param msg
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("消息的Tag：" + msg.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        producer.start();

        String[] tags = {"TAGA","TAGB","TAGC"};

        for (int i = 0; i < 3; i++) {
            Message msg = new Message("TransactionTopic",tags[i],("Hello World"+i).getBytes());
            SendResult result = producer.sendMessageInTransaction(msg, null);
            System.out.println(result);

            TimeUnit.SECONDS.sleep(1);
        }


    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.client.consumer.rebalance;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.common.message.MessageQueue;

/**
 * 负载均衡策略为平均策略
 * <p>
 * 如果创建 Topic 的时候，把 Message Queue 数设为 3
 * 1. 当 Consumer 数量为 2 的时候，有一个 Consumer 需要处理 Topic 三分之二的消息，另一个处理三分之一的消息；
 * 2. 当 Consumer 数量为 4 的时候，有一个 Consumer 无法收到消息，其他 3 个 Consumer 各处理 Topic 三分之一的消息。
 * 可见 Message Queue 数量设置过小不利于做负载均衡，通常情况下，应把一个 Topic 的 Message Queue 数设置为 16
 * Average Hashing queue algorithm
 */
public class AllocateMessageQueueAveragely extends AbstractAllocateMessageQueueStrategy {

    @Override
    public List<MessageQueue> allocate(String consumerGroup,
                                       String currentCID,
                                       List<MessageQueue> mqAll,
                                       List<String> cidAll) {

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        // 1. 校验参数是否正确（currentCID、mqAll、cidAll）
        if (!check(consumerGroup, currentCID, mqAll, cidAll)) {
            return result;
        }

        // 2. 平均分配
        int index = cidAll.indexOf(currentCID);  // 当前消费者在所有消费者中是第几个 Consumer（所以需要排序）
        int mod = mqAll.size() % cidAll.size();  // 余数，即多少消息队列无法平均分配 [0,mod] 范围的消费者可以多分配一个队列，(mod,max] 少分配一个队列
        // 每个消费者分配的队列数
        int averageSize =
                mqAll.size() <= cidAll.size() ? 1 :
                        (mod > 0 && index < mod ? mqAll.size() / cidAll.size() + 1 : mqAll.size() / cidAll.size());
        // 有余数的情况下，[0, mod) 平分余数，即每个 Consumer 多分配一个消费队列；第 index 开始，跳过前 mod 余数
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
        // 分配队列数量。之所以要 Math.min() 的原因是，mqAll.size() <= cidAll.size()，部分 consumer 分配不到消息队列（3 个 MessageQueue,4 个 Consumer,有一个消费者分配不到 MessageQueue）
        int range = Math.min(averageSize, mqAll.size() - startIndex);
        for (int i = 0; i < range; i++) {
            result.add(mqAll.get((startIndex + i) % mqAll.size()));
        }
        return result;
    }

    @Override
    public String getName() {
        return "AVG";
    }
}

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
package org.apache.rocketmq.client.consumer.listener;

/**
 * 顺序消费消息结果
 *
 * ROLLBACK 、COMMIT 暂时只使用在 MySQL binlog 场景，官方将这两状态标记为 @Deprecated。相应的实现逻辑依然保留
 */
public enum ConsumeOrderlyStatus {
    /**
     * 消费成功但不提交
     * Success consumption
     */
    SUCCESS,
    /**
     * 消费失败，消费回滚
     * Rollback consumption(only for binlog consumption)
     */
    @Deprecated
    ROLLBACK,
    /**
     * 消费成功提交并且提交
     * Commit offset(only for binlog consumption)
     */
    @Deprecated
    COMMIT,
    /**
     * 消费失败，挂起消费队列一会会，稍后继续消费
     * Suspend current queue a moment
     */
    SUSPEND_CURRENT_QUEUE_A_MOMENT;
}

/*
 * Copyright (c) 2019 The StreamX Project
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamxhub.streamx.flink.quickstart.connector;

import com.streamxhub.streamx.flink.connector.function.TransformFunction;
import com.streamxhub.streamx.flink.connector.redis.bean.RedisMapper;
import com.streamxhub.streamx.flink.connector.redis.sink.RedisSink;
import com.streamxhub.streamx.flink.core.StreamEnvConfig;
import com.streamxhub.streamx.flink.core.scala.StreamingContext;
import com.streamxhub.streamx.flink.quickstart.connector.bean.Entity;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;

/**
 * @author wudi
 **/
public class RedisJavaApp {

    public static void main(String[] args) {
        StreamEnvConfig envConfig = new StreamEnvConfig(args, null);
        StreamingContext context = new StreamingContext(envConfig);
        DataStreamSource<Entity> source = context.getJavaEnv().addSource(new MyDataSource());
        //1)定义 RedisSink
        RedisSink sink = new RedisSink(context);
        //2)写Mapper映射
        RedisMapper<Entity> flinkUser = RedisMapper.map(
                RedisCommand.HSET,
                "flink_user",
                (TransformFunction<Entity, String>) testEntity -> testEntity.userId.toString(),
                (TransformFunction<Entity, String>) testEntity -> testEntity.userId.toString()
        );
        sink.sink(source, flinkUser, 60000000).setParallelism(1);
        context.start();
    }
}

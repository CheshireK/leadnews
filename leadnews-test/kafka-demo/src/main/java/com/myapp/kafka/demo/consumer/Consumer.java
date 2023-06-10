package com.myapp.kafka.demo.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class Consumer {
    public static void main(String[] args) {
        //1.添加kafka的配置信息
        Properties prop = new Properties();
        //kafka的连接地址
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.129:9092");
        //消费者组
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        //消息的反序列化器
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // 2.创建消费者对象
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop)) {
            // 3.订阅主题
            // 需要接受一个集合
            consumer.subscribe(Collections.singletonList("my-topic"));
            // 消费某个分区的消息
            // ArrayList<TopicPartition> topicPartitions = new ArrayList<>();
            // topicPartitions.add(new TopicPartition("my-topic", 1));
            // consumer.assign(topicPartitions);

            /*
            Set<TopicPartition> assignment = new HashSet<>();
            // 分区策略的创建需要时间
            // 使用循环获取分区分配信息，当有了分配信息后才能开始消费
            while (assignment.size() == 0) {
                assignment = consumer.assignment();
            }

            // 遍历所有的分区，并指定offset从200的位置开始消费
            // 也可以指定某一个分区
            for (TopicPartition topicPartition : assignment) {
                consumer.seek(topicPartition, 200);
            }
            */

            Set<TopicPartition> assignment = new HashSet<>();
            // 分区策略的创建需要时间
            // 使用循环获取分区分配信息，当有了分配信息后才能开始消费
            while (assignment.size() == 0) {
                assignment = consumer.assignment();
            }
            Map<TopicPartition, Long> topicPartitionLongMap = new HashMap<>();
            // 将每个分区一个小时前的时间戳，保存为一个map
            for (TopicPartition topicPartition : assignment) {
                // 前一个小时的时间
                Long time = System.currentTimeMillis() - (1000 * 60 *60);
                topicPartitionLongMap.put(topicPartition, time);
            }
            // 获取每个分区时间错所对应的offset
            Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(topicPartitionLongMap);
            // 遍历每个分区，对每个分区设置offset
            for (TopicPartition topicPartition : assignment) {
                OffsetAndTimestamp timestamp = offsets.get(topicPartition);
                if (timestamp!=null){
                    consumer.seek(topicPartition, timestamp.offset());
                }
            }

            // 一直处于监听状态
            while (true){
                // 4.获取消息
                // Duration.ofMillis(1000) 拉取消息的间隔
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.print("topic=" + record.topic());
                    System.out.print(" partition=" + record.partition());
                    System.out.print(" offset=" + record.offset());
                    System.out.print(" value=" + record.value() + "\n");

                }
            }
        }


    }
}

package com.myapp.kafka.demo.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 生产者
 */
public class Producer_Transaction {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.129:9092");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        // 初始化事务
        producer.initTransactions();
        // 开启事务
        producer.beginTransaction();
        try {
            producer.send(new ProducerRecord<>("topic", "key", "value"));
            // 提交事务
            producer.commitTransaction();
        } catch (Exception e) {
            // 回滚事务
            producer.abortTransaction();
        } finally {
            producer.close();
        }
    }
}

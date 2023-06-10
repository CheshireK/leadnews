package com.myapp.kafka.demo.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 生产者
 */
public class Producer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.129:9092");
        prop.put(ProducerConfig.RETRIES_CONFIG, 1);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // prop.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.myapp.kafka.demo.config.MyPartition");

        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        Future<RecordMetadata> send = null;
        for (int i = 0; i < 100; i++) {
            String topic = "my-topic";
            String key = "";
            String value = "Hello Kafka " + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);

            send = producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception==null){
                        System.out.println("metadata.topic = " + metadata.topic());
                        System.out.println("metadata.partition = " + metadata.partition());
                    }

                }
            });
            Thread.sleep(2);
        }
        RecordMetadata recordMetadata = send.get();
        System.out.println(recordMetadata);
        // 4.关闭消息通道，必须关闭，否则消息发送不成功
        producer.close();

    }
}

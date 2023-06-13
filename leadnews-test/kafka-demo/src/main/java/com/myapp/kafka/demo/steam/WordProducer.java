package com.myapp.kafka.demo.steam;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

public class WordProducer {
    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.129:9092");
        prop.put(ProducerConfig.RETRIES_CONFIG, 1);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // prop.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.myapp.kafka.demo.config.MyPartition");
        String topic = "topic-input";
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        Reader reader = new FileReader("d:/data/alice_in_wonderland.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine())!=null){
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, line);
            producer.send(record);
            System.out.println(line);
            Thread.sleep(200);
        }

        producer.close();
    }
}

package com.myapp.kafka.demo.steam;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamQuickStart {
    public static void main(String[] args) {
        //kafka的配置信心
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.129:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams-quickstart");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> inputStream = streamsBuilder.stream("topic-input");

        KTable<Windowed<String>, Long> table = inputStream.flatMapValues((ValueMapper<String, Iterable<String>>) value -> Arrays.asList(value.split(" ")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(1)))
                .count();

        table.toStream()
                .map(((key, value) -> {
                    System.out.println("key="+key+", value="+value);
                    return new KeyValue<>(key.key().toString(), value.toString());
                }))
                .to("topic-output");


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), prop);

        kafkaStreams.start();

    }
}

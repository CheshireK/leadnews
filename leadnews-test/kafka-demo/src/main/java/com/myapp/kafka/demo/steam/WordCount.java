package com.myapp.kafka.demo.steam;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

public class WordCount implements Processor<String, String> {

    private ProcessorContext context;
    private KeyValueStore<String,Integer> kvStore;

    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
        // this.context.schedule(Duration.ofSeconds(1), PunctuationType.STREAM_TIME, Punc)
        this.context.schedule(Duration.ofSeconds(1), PunctuationType.STREAM_TIME, new Punctuator() {
            @Override
            public void punctuate(long l) {
                KeyValueIterator<String, Integer> iterator = kvStore.all();
                iterator.forEachRemaining(entry -> {
                    context.forward(entry.key, entry.value);
                    kvStore.delete(entry.key);
                });
                context.commit();
            }
        });
        this.kvStore = (KeyValueStore<String, Integer>) context.getStateStore("Counts");
    }

    @Override
    public void process(String key, String value) {
        Stream.of(value.toLowerCase().split(" "))
                .forEach((String word)->{
                    Integer count = Optional.ofNullable(kvStore.get(word))
                            .map(wordCount -> wordCount + 1)
                            .orElse(1);
                    kvStore.put(word, count);
                });
    }

    @Override
    public void close() {
        this.kvStore.close();
    }
}

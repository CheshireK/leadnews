package com.myapp.kafka.demo.config;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class MyPartition implements Partitioner {
    /**
     * Compute the partition for the given record.
     *
     * @param topic      The topic name
     * @param key        The key to partition on (or null if no key)
     * @param keyBytes   The serialized key to partition on( or null if no key)
     * @param value      The value to partition on or null
     * @param valueBytes The serialized value to partition on or null
     * @param cluster    The current cluster metadata
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        if (key!=null){
            String keystr = key.toString();
            // 当key中包含order字符时将分区设为1
            if (keystr.contains("order")){
                return 1;
            }
        }
        return 0;
    }

    /**
     * This is called when partitioner is closed.
     */
    @Override
    public void close() {

    }

    /**
     * Configure this class with the given key-value pairs
     *
     * @param configs
     */
    @Override
    public void configure(Map<String, ?> configs) {

    }
}

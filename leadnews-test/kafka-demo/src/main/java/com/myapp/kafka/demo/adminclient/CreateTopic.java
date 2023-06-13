package com.myapp.kafka.demo.adminclient;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CreateTopic {
    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "myvm.site:9092");
        // 创建AdminClient对象操作kafka
        AdminClient adminClient = AdminClient.create(prop);
        // 创建topic集合，将NewTopic对象添加到这个集合
        Collection<NewTopic> newTopics = new ArrayList<>();
        // 参数：name，分区数量，副本数量（不能超过集群的服务器数量）
        NewTopic topic1 = new NewTopic("topic-input", 1, (short) 1);
        NewTopic topic2 = new NewTopic("topic-output", 1, (short) 1);
        newTopics.add(topic1);
        newTopics.add(topic2);
        // 创建集合中所有的topic
        CreateTopicsResult topics = adminClient.createTopics(newTopics);
        try {
            topics.all().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        adminClient.close();
    }
}

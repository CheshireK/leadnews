package com.myapp.es.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
public class EsConfig {
    private String url;

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(url)));
    }


}

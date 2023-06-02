package com.myapp.file.config;

import com.myapp.file.service.FileStorageService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinIOConfigProperties.class)
@ConditionalOnClass(FileStorageService.class)
public class MinIOConfig {

    @Bean
    public MinioClient build(MinIOConfigProperties properties){
        return MinioClient.builder()
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .endpoint(properties.getEndpoint())
                .build();
    }
}

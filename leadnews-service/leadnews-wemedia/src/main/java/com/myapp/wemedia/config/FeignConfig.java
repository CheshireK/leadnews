package com.myapp.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.myapp.api.article.fallback")
public class FeignConfig {
}

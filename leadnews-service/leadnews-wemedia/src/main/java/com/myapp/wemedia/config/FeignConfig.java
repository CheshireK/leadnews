package com.myapp.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// 扫描fallback相关组件的注解
@ComponentScan(basePackages = "com.myapp.api.*.fallback")
public class FeignConfig {
}

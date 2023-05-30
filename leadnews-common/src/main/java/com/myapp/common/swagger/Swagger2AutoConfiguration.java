package com.myapp.common.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2AutoConfiguration {
    @Bean(value = "defaultApi2")
    public Docket buildDocket(ApiInfo apiInfo){
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.myapp"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public ApiInfo buildApiInfo(){
        Contact contact = new Contact("luosa", "", "luosa_acc@outlook.com");
        return new ApiInfoBuilder()
                .title("API管理文档")
                .description("新闻头条后台API")
                .contact(contact)
                .version("0.0.1")
                .build();
    }
}

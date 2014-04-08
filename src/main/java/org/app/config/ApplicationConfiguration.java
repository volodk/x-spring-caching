package org.app.config;

import org.app.service.ArticleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 1:51:01 PM 

@Configuration
@ComponentScan(basePackageClasses = { ArticleService.class })
@PropertySource("classpath:/application.properties")
public class ApplicationConfiguration {
    
    @Bean public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}

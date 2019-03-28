package com.sendi;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.sendi.dao")
@EnableScheduling
public class SendicloudApplication extends SpringBootServletInitializer {
    private  static  final Logger logger = LoggerFactory.getLogger(SendicloudApplication.class);
    public static void main(String[] args) {
        logger.info("start**********************");
        SpringApplication.run(SendicloudApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SendicloudApplication.class);
    }

}

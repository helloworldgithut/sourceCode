package com.sendi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
/**
    * @Author Mengfeng Qin
    * @Description 上传文件配置
    * @Date 2019/3/26 18:15
*/
@Configuration
public class Config {
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        return  new CommonsMultipartResolver();
    }

}

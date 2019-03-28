package com.sendi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/***
    * @Author Mengfeng Qin
    * @Description
    * @Date 2019/3/26 18:20
*/
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	private static final String PACKAGE = "com.sendi";
	
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage(PACKAGE))
				// 配置了@ApiOperation的注解的才暴露给swagger
//				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any())
				.build();
	}
	
	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("平台接口文档")
				.description("接口列表，可以直接在此调试接口")
				.termsOfServiceUrl("")
				.version("V1.0")
				.build();
	}

}

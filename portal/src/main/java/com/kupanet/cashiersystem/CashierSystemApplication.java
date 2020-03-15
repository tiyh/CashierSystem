package com.kupanet.cashiersystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@MapperScan("com.kupanet.cashiersystem.DAO")
//@SpringBootApplication(exclude = VelocityAutoConfiguration.class)
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class CashierSystemApplication {
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(CashierSystemApplication.class, args);
	}
}

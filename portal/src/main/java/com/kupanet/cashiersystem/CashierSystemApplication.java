package com.kupanet.cashiersystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@MapperScan("com.kupanet.cashiersystem.DAO")
//@SpringBootApplication(exclude = VelocityAutoConfiguration.class)
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class CashierSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashierSystemApplication.class, args);
	}
}

package com.kupanet.cashiersystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;


@MapperScan("com.kupanet.cashiersystem.DAO")
@SpringBootApplication(exclude = VelocityAutoConfiguration.class)
public class CashierSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashierSystemApplication.class, args);
	}
}

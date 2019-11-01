package com.kupanet.cashiersystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kupanet.cashiersystem.DAO")
public class CashierSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashierSystemApplication.class, args);
	}
}

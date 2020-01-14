package com.kupanet.leaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class LeafSnowFlakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeafSnowFlakeApplication.class, args);
	}

}

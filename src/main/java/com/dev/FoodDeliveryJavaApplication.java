package com.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodDeliveryJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryJavaApplication.class, args);
	}

}

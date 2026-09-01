package com.backend.dealspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DealspotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DealspotApplication.class, args);
	}

}

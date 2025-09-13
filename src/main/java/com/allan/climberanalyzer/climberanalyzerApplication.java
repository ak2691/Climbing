package com.allan.climberanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class climberanalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(climberanalyzerApplication.class, args);
	}

}

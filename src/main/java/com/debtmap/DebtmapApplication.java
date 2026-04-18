package com.debtmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DebtmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebtmapApplication.class, args);
	}
}
package com.etiya.crm.dataseeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DataSeederApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataSeederApplication.class, args);
	}
}

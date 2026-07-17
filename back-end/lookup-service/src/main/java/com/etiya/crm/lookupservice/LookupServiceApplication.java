package com.etiya.crm.lookupservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "jwtAuditorAware")
@SpringBootApplication
public class LookupServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LookupServiceApplication.class, args);
	}

}

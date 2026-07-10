package com.etiya.crm.contactinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ContactInfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactInfoServiceApplication.class, args);
	}

}

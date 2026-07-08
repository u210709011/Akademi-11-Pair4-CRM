package com.etiya.crm.customerservice.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Bean Validation (@NotBlank vb.) mesaj key'lerini ("{validation.field.required}")
 * Spring Boot'un varsayilan ValidationMessages bundle'i yerine bizim
 * messages/messages.properties dosyamiz uzerinden coz. Boylece DTO
 * validasyonlari ve business exception'lar ayni MessageSource'u paylasir.
 */
@Configuration
public class ValidationConfig {

	@Bean
	public LocalValidatorFactoryBean validator(MessageSource messageSource) {
		LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
		factoryBean.setValidationMessageSource(messageSource);
		return factoryBean;
	}
}

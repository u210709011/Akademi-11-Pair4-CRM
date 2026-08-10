package com.etiya.crm.productservice.config;

import java.nio.charset.StandardCharsets;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * UI-10: Spring Boot'un otomatik yapilandirdigi MessageSource, .properties dosyalarini
 * config-server'daki (bu repoda olmayan) spring.messages.encoding degerine bagli okur.
 * Turkce karakterler ("olu?tu" gibi) bu deger UTF-8 olmayinca bozuluyordu. Basename/encoding'i
 * burada acikca sabitleyerek merkezi config'ten bagimsiz, garanti dogru okuma sagliyoruz.
 */
@Configuration
public class MessageSourceConfig {

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages/messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		return messageSource;
	}
}

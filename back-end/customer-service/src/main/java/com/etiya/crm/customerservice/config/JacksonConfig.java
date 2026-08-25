package com.etiya.crm.customerservice.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;


@Configuration
public class JacksonConfig {

	@Bean
	public Module blankStringToNullModule() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new StringDeserializer() {
			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
				String value = super.deserialize(p, ctxt);
				return value != null && value.isBlank() ? null : value;
			}
		});
		return module;
	}
}

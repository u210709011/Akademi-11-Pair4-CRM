package com.etiya.crm.customerservice.config;

import java.io.IOException;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Opsiyonel string alanlara (homePhone, fax vb.) bos ya da sadece bosluktan olusan bir
 * deger gonderilmesini "girilmemis" (null) sayar. Aksi halde @Pattern null'i atlarken bos
 * string'i de kontrol ediyor - kullanici dokunmadigi bir alan yuzunden 400 aliyordu. Boylece
 * bugun homePhone/fax, yarin eklenecek her opsiyonel string alan icin ayni davranis gecerli
 * olur - her DTO'da desene "^$|..." eklemeyi hatirlamaya bagimli kalinmaz.
 */
@Configuration
public class JacksonConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer blankStringToNullCustomizer() {
		return builder -> builder.modulesToInstall(blankStringToNullModule());
	}

	private SimpleModule blankStringToNullModule() {
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

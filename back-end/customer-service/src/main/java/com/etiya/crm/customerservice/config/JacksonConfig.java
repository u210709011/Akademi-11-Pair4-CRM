package com.etiya.crm.customerservice.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Opsiyonel string alanlara (homePhone, fax vb.) bos ya da sadece bosluktan olusan bir
 * deger gonderilmesini "girilmemis" (null) sayar. Aksi halde @Pattern null'i atlarken bos
 * string'i de kontrol ediyor - kullanici dokunmadigi bir alan yuzunden 400 aliyordu. Boylece
 * bugun homePhone/fax, yarin eklenecek her opsiyonel string alan icin ayni davranis gecerli
 * olur - her DTO'da desene "^$|..." eklemeyi hatirlamaya bagimli kalinmaz.
 *
 * B-24: bu modul bilerek bir Jackson2ObjectMapperBuilderCustomizer icinde
 * builder.modulesToInstall(...) ile DEGIL, duz bir Module @Bean'i olarak tanimlanir.
 * JacksonAutoConfiguration'in kendi standardJacksonObjectMapperBuilderCustomizer'i context'teki
 * TUM Module bean'lerini (Spring Data'nin Page/Sort modulu dahil) tek bir modulesToInstall(...)
 * cagrisinda toplar; modulesToInstall REPLACE eder, APPEND etmez - iki ayri customizer kendi
 * modulesToInstall'ini cagirirsa sonuncusu digerini eziyordu (Sort meta verisi sayfali
 * yanitlardan kayboluyordu). Module bean'i olarak sunmak, otomatik toplamaya dahil olup bu
 * catismayi onler.
 */
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

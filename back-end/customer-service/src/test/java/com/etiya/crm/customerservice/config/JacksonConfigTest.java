package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;

import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * homePhone/fax gibi opsiyonel alanlara "" ya da sadece bosluk gonderilmesi, null gonderilmesiyle
 * ayni davranmali - aksi halde @Pattern bos string'i de kontrol ediyor (null'u atlarken) ve
 * dokunulmamis bir alan yuzunden 400 donuyordu (bkz. JacksonConfig javadoc'u).
 */
class JacksonConfigTest {

	@Test
	void emptyString_deserializesAsNull() throws Exception {
		ObjectMapper mapper = buildMapper();

		ContactInfo contactInfo = mapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"","fax":""}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isNull();
		assertThat(contactInfo.fax()).isNull();
	}

	@Test
	void whitespaceOnlyString_deserializesAsNull() throws Exception {
		ObjectMapper mapper = buildMapper();

		ContactInfo contactInfo = mapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"   ","fax":"\\t"}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isNull();
		assertThat(contactInfo.fax()).isNull();
	}

	@Test
	void nonBlankString_isUnaffected() throws Exception {
		ObjectMapper mapper = buildMapper();

		ContactInfo contactInfo = mapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"2121234567","fax":"1234567890"}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isEqualTo("2121234567");
		assertThat(contactInfo.fax()).isEqualTo("1234567890");
	}

	private ObjectMapper buildMapper() {
		Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
		new JacksonConfig().blankStringToNullCustomizer().customize(builder);
		return builder.build();
	}
}

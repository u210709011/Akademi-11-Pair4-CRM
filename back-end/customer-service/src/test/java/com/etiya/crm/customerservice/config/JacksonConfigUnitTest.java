package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * blankStringToNullModule()'un deserializer davranisini, tam Spring context'i
 * (JacksonConfigTest @SpringBootTest'te, sadece CI'da degil) baslatmadan
 * dogrudan dogrular - saf bir Jackson Module, context'e ihtiyaci yok.
 */
class JacksonConfigUnitTest {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JacksonConfig().blankStringToNullModule());

	private record SingleField(String value) {
	}

	@Test
	void emptyString_deserializesAsNull() throws Exception {
		SingleField result = objectMapper.readValue("{\"value\":\"\"}", SingleField.class);

		assertThat(result.value()).isNull();
	}

	@Test
	void whitespaceOnlyString_deserializesAsNull() throws Exception {
		SingleField result = objectMapper.readValue("{\"value\":\"   \"}", SingleField.class);

		assertThat(result.value()).isNull();
	}

	@Test
	void nonBlankString_isUnaffected() throws Exception {
		SingleField result = objectMapper.readValue("{\"value\":\"hello\"}", SingleField.class);

		assertThat(result.value()).isEqualTo("hello");
	}
}

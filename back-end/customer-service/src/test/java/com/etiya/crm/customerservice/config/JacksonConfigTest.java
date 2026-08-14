package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * homePhone/fax gibi opsiyonel alanlara "" ya da sadece bosluk gonderilmesi, null gonderilmesiyle
 * ayni davranmali - aksi halde @Pattern bos string'i de kontrol ediyor (null'u atlarken) ve
 * dokunulmamis bir alan yuzunden 400 donuyordu (bkz. JacksonConfig javadoc'u).
 *
 * B-24 regresyonu: burada gercek uygulama context'inden auto-configured ObjectMapper bean'i
 * enjekte edilir (elle kurulmus, izole bir mapper degil) - amac tam olarak production'da kirilan
 * teli test etmek: Spring Data'nin Sort/Page Jackson modulunun blankStringToNullModule tarafindan
 * ezilmedigini dogrulamak.
 */
@SpringBootTest
class JacksonConfigTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void emptyString_deserializesAsNull() throws Exception {
		ContactInfo contactInfo = objectMapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"","fax":""}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isNull();
		assertThat(contactInfo.fax()).isNull();
	}

	@Test
	void whitespaceOnlyString_deserializesAsNull() throws Exception {
		ContactInfo contactInfo = objectMapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"   ","fax":"\\t"}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isNull();
		assertThat(contactInfo.fax()).isNull();
	}

	@Test
	void nonBlankString_isUnaffected() throws Exception {
		ContactInfo contactInfo = objectMapper.readValue(
				"""
				{"email":"a@b.com","mobilePhone":"5551234567","homePhone":"2121234567","fax":"1234567890"}
				""",
				ContactInfo.class);

		assertThat(contactInfo.homePhone()).isEqualTo("2121234567");
		assertThat(contactInfo.fax()).isEqualTo("1234567890");
	}

	/**
	 * B-24: blankStringToNullModule bir Jackson2ObjectMapperBuilderCustomizer icinde
	 * modulesToInstall(...) ile eklenseydi, Spring Boot'un kendi standard customizer'inin
	 * (Spring Data'nin Sort/Page modulunu de iceren) modulesToInstall cagrisini ezip Sort'u
	 * duz bir POJO gibi ({"empty":...,"sorted":...,"unsorted":...}) serilestirirdi. Dogru
	 * davranista Sort bir JSON DIZISI olarak yazilir.
	 */
	@Test
	void sortSerialization_stillUsesSpringDataModule_notPlainBeanSerialization() throws Exception {
		Sort sort = Sort.by(Sort.Order.asc("firstName"));

		String json = objectMapper.writeValueAsString(sort);

		assertThat(json).startsWith("[").doesNotContain("\"unsorted\"").doesNotContain("\"empty\"");
	}
}

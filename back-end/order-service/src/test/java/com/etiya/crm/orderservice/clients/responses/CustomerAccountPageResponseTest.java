package com.etiya.crm.orderservice.clients.responses;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * customer-service GET /customers/{custId}/accounts, Spring Data Page<> serialize eder
 * ({"content":[...],"pageable":{...},...}). CustomerClient.getAccounts() bunu duz List
 * saniyordu (bkz. commit history) ve Feign decode'u sessizce patliyordu - bu test, gercek
 * bir sunucu ayaga kaldirmadan sadece JSON sekli degisirse kirilacak sekilde onu tekrar
 * yakalar.
 *
 * FAIL_ON_UNKNOWN_PROPERTIES elle kapatilir - Spring Boot'un otomatik yapilandirdigi
 * ObjectMapper bunu varsayilan olarak kapatir (aksi halde "pageable" gibi bilinmeyen
 * alanlar production'da calisip burada patlar).
 */
class CustomerAccountPageResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Test
	void deserializes_customerServicePageEnvelope_intoContentList() throws Exception {
		String json = """
				{
				  "content": [
				    { "custAcctId": 2, "accountNo": "TESTACC-001", "accountName": "Test Account",
				      "accountDesc": null, "addressId": null, "accountTpId": 223, "acctStId": null, "active": true }
				  ],
				  "pageable": { "pageNumber": 0, "pageSize": 1000, "sort": [] },
				  "totalElements": 1,
				  "totalPages": 1,
				  "last": true,
				  "first": true,
				  "numberOfElements": 1,
				  "empty": false
				}
				""";

		CustomerAccountPageResponse response = objectMapper.readValue(json, CustomerAccountPageResponse.class);

		assertThat(response.content()).hasSize(1);
		assertThat(response.content().get(0).custAcctId()).isEqualTo(2L);
		assertThat(response.content().get(0).accountNo()).isEqualTo("TESTACC-001");
	}

	@Test
	void deserializes_emptyPage() throws Exception {
		String json = """
				{ "content": [], "pageable": {}, "totalElements": 0, "empty": true }
				""";

		CustomerAccountPageResponse response = objectMapper.readValue(json, CustomerAccountPageResponse.class);

		assertThat(response.content()).isEmpty();
	}
}

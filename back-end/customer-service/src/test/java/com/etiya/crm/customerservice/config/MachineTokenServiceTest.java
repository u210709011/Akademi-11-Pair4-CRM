package com.etiya.crm.customerservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * client_credentials token'i her Kafka mesaji icin yeniden istememek adina
 * bellekte cache'lenir - bu testler cache/expiry davranisini dogrular.
 * Instant.now() enjekte edilebilir bir Clock DEGIL; expiry siniri, gercek
 * zamanla oynamadan expiresIn degeriyle kontrol edilir (0 -> hemen "suresi
 * dolmus" sayilir, guvenlik payi -30s oldugu icin).
 */
@ExtendWith(MockitoExtension.class)
class MachineTokenServiceTest {

	@Mock
	private RestClient restClient;

	@Mock
	private RestClient.RequestBodyUriSpec requestBodyUriSpec;

	@Mock
	private RestClient.RequestBodySpec requestBodySpec;

	@Mock
	private RestClient.ResponseSpec responseSpec;

	@Mock
	private KeycloakProperties keycloakProperties;

	private MachineTokenService machineTokenService;

	private void stubTokenResponse(String accessToken, long expiresIn) {
		when(restClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
		// RequestBodySpec.body(...) birden fazla overload'a sahiptir (generic <T> body(T),
		// StreamingHttpOutputMessage.Body body(...) vb.) - tipsiz any() derleme zamaninda
		// yanlis overload'a baglanip gercek .body(form) cagrisini eslesmeden birakabilir
		// (zincir null doner, .retrieve() sonra NPE atar). Tip witness'i ile generic
		// overload'u ACIKCA hedeflemek gerekiyor.
		when(requestBodySpec.body(ArgumentMatchers.<MultiValueMap<String, String>>any())).thenReturn(requestBodySpec);
		when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(KeycloakTokenResponse.class))
				.thenReturn(new KeycloakTokenResponse(accessToken, expiresIn));
	}

	@Test
	void getAccessToken_fetchesToken_onFirstCall() {
		machineTokenService = new MachineTokenService(restClient, keycloakProperties);
		stubTokenResponse("token-1", 300L);

		String token = machineTokenService.getAccessToken();

		assertThat(token).isEqualTo("token-1");
		verify(restClient, times(1)).post();
	}

	@Test
	void getAccessToken_reusesCachedToken_whenNotExpired() {
		machineTokenService = new MachineTokenService(restClient, keycloakProperties);
		stubTokenResponse("token-1", 3600L);

		String first = machineTokenService.getAccessToken();
		String second = machineTokenService.getAccessToken();

		assertThat(first).isEqualTo("token-1");
		assertThat(second).isEqualTo("token-1");
		verify(restClient, times(1)).post();
	}

	@Test
	void getAccessToken_fetchesNewToken_whenCachedTokenAlreadyExpired() {
		machineTokenService = new MachineTokenService(restClient, keycloakProperties);
		// expiresIn=0 - 30sn guvenlik payiyla birlikte cache'lenir cache'lenmez "suresi dolmus" sayilir.
		stubTokenResponse("token-1", 0L);

		String first = machineTokenService.getAccessToken();

		stubTokenResponse("token-2", 3600L);
		String second = machineTokenService.getAccessToken();

		assertThat(first).isEqualTo("token-1");
		assertThat(second).isEqualTo("token-2");
		verify(restClient, times(2)).post();
	}
}

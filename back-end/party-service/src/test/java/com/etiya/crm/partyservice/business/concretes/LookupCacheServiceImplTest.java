package com.etiya.crm.partyservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.business.exceptions.LookupValueNotFoundException;
import com.etiya.crm.partyservice.clients.LookupClient;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LookupCacheServiceImplTest {

	@Mock
	private LookupClient lookupClient;

	@InjectMocks
	private LookupCacheServiceImpl service;

	@Test
	void resolveIdByCode_returnsId_whenFound() {
		when(lookupClient.resolveType("CAM_PARTY_TYPE", "INDV"))
				.thenReturn(new GnlTpResponse(10L, "Individual", null, "INDV", "CAM_PARTY_TYPE", null, true, null,
						null, null, null));

		assertThat(service.resolveIdByCode("CAM_PARTY_TYPE", "INDV")).isEqualTo(10L);
	}

	@Test
	void resolveIdByCode_throws_whenResponseIsNull() {
		when(lookupClient.resolveType("CAM_PARTY_TYPE", "UNKNOWN")).thenReturn(null);

		assertThatThrownBy(() -> service.resolveIdByCode("CAM_PARTY_TYPE", "UNKNOWN"))
				.isInstanceOf(LookupValueNotFoundException.class);
	}

	@Test
	void resolveIdByCode_throws_whenIdIsNull() {
		when(lookupClient.resolveType("CAM_PARTY_TYPE", "UNKNOWN"))
				.thenReturn(new GnlTpResponse(null, null, null, null, null, null, false, null, null, null, null));

		assertThatThrownBy(() -> service.resolveIdByCode("CAM_PARTY_TYPE", "UNKNOWN"))
				.isInstanceOf(LookupValueNotFoundException.class);
	}
}

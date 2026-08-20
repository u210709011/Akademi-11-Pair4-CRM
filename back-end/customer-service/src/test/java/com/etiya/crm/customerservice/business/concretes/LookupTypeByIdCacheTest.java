package com.etiya.crm.customerservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @Cacheable davranisi (proxy uzerinden calisir) burada test edilmiyor, sadece delegasyon. */
@ExtendWith(MockitoExtension.class)
class LookupTypeByIdCacheTest {

	@Mock
	private LookupClient lookupClient;

	@InjectMocks
	private LookupTypeByIdCache cache;

	@Test
	void getTypeById_delegatesToLookupClient() {
		GnlTpResponse response = new GnlTpResponse(5L, "Istanbul", null, "IST", "CITY", "Sehir", true, null, null,
				null, null);
		when(lookupClient.getTypeById(5L)).thenReturn(response);

		GnlTpResponse result = cache.getTypeById(5L);

		assertThat(result).isSameAs(response);
		verify(lookupClient).getTypeById(5L);
	}
}

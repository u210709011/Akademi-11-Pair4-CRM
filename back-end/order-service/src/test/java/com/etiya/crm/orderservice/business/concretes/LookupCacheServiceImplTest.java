package com.etiya.crm.orderservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.orderservice.clients.controllers.LookupClient;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LookupCacheServiceImplTest {

	@Mock
	private LookupClient lookupClient;

	@InjectMocks
	private LookupCacheServiceImpl lookupCacheService;

	@Test
	void resolveStatusId_delegatesToLookupClient() {
		GnlStResponse response = new GnlStResponse(5L, "Wait", "Wait", "WAIT", true, "CUST_ORD", "CUST_ORD",
				null, null, null, null);
		when(lookupClient.resolveGeneralStatus("CUST_ORD", "WAIT")).thenReturn(response);

		assertThat(lookupCacheService.resolveStatusId("CUST_ORD", "WAIT")).isEqualTo(5L);
	}

	@Test
	void resolveDataTypeId_delegatesToLookupClient() {
		TypeValueResponse response = new TypeValueResponse(1L, "CUST", 12L, "desc", "val", "module", null, null,
				null, null);
		when(lookupClient.getTypeValueByTable("CUST")).thenReturn(response);

		assertThat(lookupCacheService.resolveDataTypeId("CUST")).isEqualTo(12L);
	}

	@Test
	void getCharacteristic_delegatesToLookupClient() {
		GnlCharResponse response = new GnlCharResponse(1L, "Color", "desc", "cls", "COLOR", true, null, null,
				null, null);
		when(lookupClient.getCharacteristicById(1L)).thenReturn(response);

		assertThat(lookupCacheService.getCharacteristic(1L)).isEqualTo(response);
	}

	@Test
	void getCharacteristicValue_delegatesToLookupClient() {
		GnlCharValResponse response = new GnlCharValResponse(1L, 2L, true, "Red", "RED", null, null, true, null,
				null, null, null);
		when(lookupClient.getCharacteristicValueById(1L)).thenReturn(response);

		assertThat(lookupCacheService.getCharacteristicValue(1L)).isEqualTo(response);
	}

	@Test
	void getGeneralType_delegatesToLookupClient() {
		GnlTpResponse response = new GnlTpResponse(1L, "Istanbul", "desc", "IST", "CITY", "CITY", true, null,
				null, null, null);
		when(lookupClient.getGeneralTypeById(1L)).thenReturn(response);

		assertThat(lookupCacheService.getGeneralType(1L)).isEqualTo(response);
	}

	@Test
	void existsInGroup_returnsFalse_whenIdIsNull() {
		assertThat(lookupCacheService.existsInGroup(null, "CITY")).isFalse();
	}

	@Test
	void existsInGroup_returnsTrue_whenActiveAndEntCodeNameMatches() {
		GnlTpResponse response = new GnlTpResponse(1L, "Istanbul", "desc", "IST", "CITY", "CITY", true, null,
				null, null, null);
		when(lookupClient.getGeneralTypeById(1L)).thenReturn(response);

		assertThat(lookupCacheService.existsInGroup(1L, "CITY")).isTrue();
	}

	@Test
	void existsInGroup_returnsFalse_whenEntCodeNameDoesNotMatch() {
		GnlTpResponse response = new GnlTpResponse(1L, "Istanbul", "desc", "IST", "CITY", "CITY", true, null,
				null, null, null);
		when(lookupClient.getGeneralTypeById(1L)).thenReturn(response);

		assertThat(lookupCacheService.existsInGroup(1L, "COUNTRY")).isFalse();
	}

	@Test
	void existsInGroup_returnsFalse_whenTypeIsInactive() {
		GnlTpResponse response = new GnlTpResponse(1L, "Istanbul", "desc", "IST", "CITY", "CITY", false, null,
				null, null, null);
		when(lookupClient.getGeneralTypeById(1L)).thenReturn(response);

		assertThat(lookupCacheService.existsInGroup(1L, "CITY")).isFalse();
	}

	@Test
	void existsInGroup_returnsFalse_whenLookupClientThrows() {
		when(lookupClient.getGeneralTypeById(1L)).thenThrow(new RuntimeException("downstream unavailable"));

		assertThat(lookupCacheService.existsInGroup(1L, "CITY")).isFalse();
	}

}

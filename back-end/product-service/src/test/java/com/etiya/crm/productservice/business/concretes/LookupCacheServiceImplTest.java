package com.etiya.crm.productservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.exceptions.CharacteristicNotFoundException;
import com.etiya.crm.productservice.business.exceptions.CharacteristicValueNotFoundException;
import com.etiya.crm.productservice.business.exceptions.LookupValueNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ResourceSpecNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ServiceSpecNotFoundException;
import com.etiya.crm.productservice.clients.LookupClient;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LookupCacheServiceImplTest {

	@Mock
	private LookupClient lookupClient;

	private LookupCacheServiceImpl lookupCacheService() {
		return new LookupCacheServiceImpl(lookupClient);
	}

	@Test
	void resolveTypeIdByCode_returnsId_whenFound() {
		when(lookupClient.resolveType("PROD", "ACTV"))
				.thenReturn(new GnlTpResponse(1L, "Active", "desc", "ACTV", "PROD", "PROD", true, null, null, null, null));

		assertThat(lookupCacheService().resolveTypeIdByCode("PROD", "ACTV")).isEqualTo(1L);
	}

	@Test
	void resolveTypeIdByCode_throws_whenResponseIsNull() {
		when(lookupClient.resolveType("PROD", "UNKNOWN")).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().resolveTypeIdByCode("PROD", "UNKNOWN"))
				.isInstanceOf(LookupValueNotFoundException.class);
	}

	@Test
	void resolveStatusIdByCode_returnsId_whenFound() {
		when(lookupClient.resolveStatus("PROD", "ACTV"))
				.thenReturn(new GnlStResponse(1L, "Active", "desc", "ACTV", true, "PROD", "PROD", null, null, null, null));

		assertThat(lookupCacheService().resolveStatusIdByCode("PROD", "ACTV")).isEqualTo(1L);
	}

	@Test
	void resolveStatusIdByCode_throws_whenResponseIsNull() {
		when(lookupClient.resolveStatus("PROD", "UNKNOWN")).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().resolveStatusIdByCode("PROD", "UNKNOWN"))
				.isInstanceOf(LookupValueNotFoundException.class);
	}

	@Test
	void validateResourceSpecId_returnsId_whenFound() {
		when(lookupClient.getResourceSpecById(1L))
				.thenReturn(new RsrcSpecResponse(1L, "SIM Kart", "desc", 1L, "SIM", null, null, null, null));

		assertThat(lookupCacheService().validateResourceSpecId(1L)).isEqualTo(1L);
	}

	@Test
	void validateResourceSpecId_throws_whenResponseIsNull() {
		when(lookupClient.getResourceSpecById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().validateResourceSpecId(1L))
				.isInstanceOf(ResourceSpecNotFoundException.class);
	}

	@Test
	void validateServiceSpecId_returnsId_whenFound() {
		when(lookupClient.getServiceSpecById(1L))
				.thenReturn(new SrvcSpecResponse(1L, "Aktivasyon", "desc", "ACT", 1L, null, null, null, null));

		assertThat(lookupCacheService().validateServiceSpecId(1L)).isEqualTo(1L);
	}

	@Test
	void validateServiceSpecId_throws_whenResponseIsNull() {
		when(lookupClient.getServiceSpecById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().validateServiceSpecId(1L))
				.isInstanceOf(ServiceSpecNotFoundException.class);
	}

	@Test
	void validateCharacteristicId_returnsId_whenFound() {
		when(lookupClient.getCharacteristicById(1L))
				.thenReturn(new GnlCharResponse(1L, "Color", "desc", "cls", "COLOR", true, null, null, null, null));

		assertThat(lookupCacheService().validateCharacteristicId(1L)).isEqualTo(1L);
	}

	@Test
	void validateCharacteristicId_throws_whenResponseIsNull() {
		when(lookupClient.getCharacteristicById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().validateCharacteristicId(1L))
				.isInstanceOf(CharacteristicNotFoundException.class);
	}

	@Test
	void validateCharacteristicValueId_returnsId_whenFound() {
		when(lookupClient.getCharacteristicValueById(1L))
				.thenReturn(new GnlCharValResponse(1L, 2L, true, "Red", "RED", null, null, true, null, null, null, null));

		assertThat(lookupCacheService().validateCharacteristicValueId(1L)).isEqualTo(1L);
	}

	@Test
	void validateCharacteristicValueId_throws_whenResponseIsNull() {
		when(lookupClient.getCharacteristicValueById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().validateCharacteristicValueId(1L))
				.isInstanceOf(CharacteristicValueNotFoundException.class);
	}

	@Test
	void getCharacteristicName_returnsName_whenFound() {
		when(lookupClient.getCharacteristicById(1L))
				.thenReturn(new GnlCharResponse(1L, "Color", "desc", "cls", "COLOR", true, null, null, null, null));

		assertThat(lookupCacheService().getCharacteristicName(1L)).isEqualTo("Color");
	}

	@Test
	void getCharacteristicName_throws_whenResponseIsNull() {
		when(lookupClient.getCharacteristicById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().getCharacteristicName(1L))
				.isInstanceOf(CharacteristicNotFoundException.class);
	}

	@Test
	void getCharacteristicValueName_returnsVal_whenFound() {
		when(lookupClient.getCharacteristicValueById(1L))
				.thenReturn(new GnlCharValResponse(1L, 2L, true, "Red", "RED", null, null, true, null, null, null, null));

		assertThat(lookupCacheService().getCharacteristicValueName(1L)).isEqualTo("Red");
	}

	@Test
	void getCharacteristicValueName_throws_whenResponseIsNull() {
		when(lookupClient.getCharacteristicValueById(1L)).thenReturn(null);

		assertThatThrownBy(() -> lookupCacheService().getCharacteristicValueName(1L))
				.isInstanceOf(CharacteristicValueNotFoundException.class);
	}

}

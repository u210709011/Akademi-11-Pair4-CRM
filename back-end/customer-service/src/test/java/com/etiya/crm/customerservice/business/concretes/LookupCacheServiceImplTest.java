package com.etiya.crm.customerservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.clients.controllers.LookupClient;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LookupCacheServiceImplTest {

	@Mock
	private LookupClient lookupClient;

	@Mock
	private LookupTypeByIdCache typeByIdCache;

	@InjectMocks
	private LookupCacheServiceImpl service;

	@Test
	void resolveTypeId_returnsGnlTpId() {
		when(lookupClient.resolveType("PARTY", "IND")).thenReturn(gnlTp(10L, "IND", "PARTY", true));

		assertThat(service.resolveTypeId("PARTY", "IND")).isEqualTo(10L);
	}

	@Test
	void resolveStatusId_returnsGnlStId() {
		when(lookupClient.resolveStatus("CUST_ACCT", "ACTV"))
				.thenReturn(new GnlStResponse(601L, "Active", null, "ACTV", true, "CUST_ACCT", null, null, null, null,
						null));

		assertThat(service.resolveStatusId("CUST_ACCT", "ACTV")).isEqualTo(601L);
	}

	@Test
	void resolveDataTypeId_returnsFieldName() {
		when(lookupClient.getTypeValueByTableName("CUST"))
				.thenReturn(new TypeValueResponse(1L, "CUST", 5L, null, null, null, null, null, null, null));

		assertThat(service.resolveDataTypeId("CUST")).isEqualTo(5L);
	}

	@Test
	void resolveTypeValue_returnsName() {
		when(lookupClient.getTypeById(10L)).thenReturn(gnlTp(10L, "IND", "PARTY", true));

		assertThat(service.resolveTypeValue(10L)).isEqualTo("Individual");
	}

	@Test
	void resolveTypeShrtCode_delegatesToTypeByIdCache() {
		when(typeByIdCache.getTypeById(10L)).thenReturn(gnlTp(10L, "IND", "PARTY", true));

		assertThat(service.resolveTypeShrtCode(10L)).isEqualTo("IND");
	}

	@Test
	void existsInGroup_false_whenIdIsNull() {
		assertThat(service.existsInGroup(null, "PARTY")).isFalse();
	}

	@Test
	void existsInGroup_true_whenActiveAndMatchingGroup() {
		when(typeByIdCache.getTypeById(10L)).thenReturn(gnlTp(10L, "IND", "PARTY", true));

		assertThat(service.existsInGroup(10L, "PARTY")).isTrue();
	}

	@Test
	void existsInGroup_false_whenGroupDiffers() {
		when(typeByIdCache.getTypeById(10L)).thenReturn(gnlTp(10L, "IND", "PARTY", true));

		assertThat(service.existsInGroup(10L, "OTHER_GROUP")).isFalse();
	}

	@Test
	void existsInGroup_false_whenInactive() {
		when(typeByIdCache.getTypeById(10L)).thenReturn(gnlTp(10L, "IND", "PARTY", false));

		assertThat(service.existsInGroup(10L, "PARTY")).isFalse();
	}

	@Test
	void existsInGroup_false_whenDownstreamCallFails() {
		when(typeByIdCache.getTypeById(10L)).thenThrow(new RuntimeException("lookup-service down"));

		assertThat(service.existsInGroup(10L, "PARTY")).isFalse();
	}

	private GnlTpResponse gnlTp(Long id, String shrtCode, String entCodeName, boolean active) {
		return new GnlTpResponse(id, "Individual", null, shrtCode, entCodeName, null, active, null, null, null, null);
	}
}

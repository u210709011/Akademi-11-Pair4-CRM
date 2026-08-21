package com.etiya.crm.customerservice.business.concretes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.typevalue.TypeValueTables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** Her metot sadece dogru grup/kod ile LookupCacheService'e delege ediyor mu, onu dogrular. */
@ExtendWith(MockitoExtension.class)
class CustomerLookupResolverImplTest {

	@Mock
	private LookupCacheService lookupCacheService;

	@InjectMocks
	private CustomerLookupResolverImpl resolver;

	@Test
	void resolveCustomerDataTypeId_usesCustomerTable() {
		when(lookupCacheService.resolveDataTypeId(TypeValueTables.CUSTOMER)).thenReturn(1L);

		assertThat(resolver.resolveCustomerDataTypeId()).isEqualTo(1L);
	}

	@Test
	void resolveContactMediumTypeId_usesContactMediumGroup() {
		when(lookupCacheService.resolveTypeId(GnlTpGroups.CONTACT_MEDIUM, "EMAIL")).thenReturn(2L);

		assertThat(resolver.resolveContactMediumTypeId("EMAIL")).isEqualTo(2L);
	}

	@Test
	void resolveActiveAccountStatusId_usesActiveCode() {
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.ACTIVE)).thenReturn(601L);

		assertThat(resolver.resolveActiveAccountStatusId()).isEqualTo(601L);
	}

	@Test
	void resolvePassiveAccountStatusId_usesPassiveCode() {
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.PASSIVE)).thenReturn(602L);

		assertThat(resolver.resolvePassiveAccountStatusId()).isEqualTo(602L);
	}

	@Test
	void resolveDeletedAccountStatusId_usesDeletedCode() {
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.DELETED)).thenReturn(603L);

		assertThat(resolver.resolveDeletedAccountStatusId()).isEqualTo(603L);
	}

	@Test
	void resolveCustomerAccountTypeId_usesAccountTypeGroup() {
		when(lookupCacheService.resolveTypeId(GnlTpGroups.ACCOUNT_TYPE, GnlTpCodes.CUSTOMER_ACCOUNT))
				.thenReturn(500L);

		assertThat(resolver.resolveCustomerAccountTypeId()).isEqualTo(500L);
	}

	@Test
	void resolveBillingAccountTypeId_usesAccountTypeGroup() {
		when(lookupCacheService.resolveTypeId(GnlTpGroups.ACCOUNT_TYPE, GnlTpCodes.BILLING_ACCOUNT))
				.thenReturn(501L);

		assertThat(resolver.resolveBillingAccountTypeId()).isEqualTo(501L);
	}

	@Test
	void resolveIndividualCustomerTypeId_usesCustomerTypeGroup() {
		when(lookupCacheService.resolveTypeId(GnlTpGroups.CUSTOMER_TYPE, GnlTpCodes.INDIVIDUAL_CUSTOMER))
				.thenReturn(1L);

		assertThat(resolver.resolveIndividualCustomerTypeId()).isEqualTo(1L);
	}

	@Test
	void resolveCustomerRoleTypeId_usesPartyRoleTypeGroup() {
		when(lookupCacheService.resolveTypeId(GnlTpGroups.PARTY_ROLE_TYPE, GnlTpCodes.CUSTOMER_ROLE))
				.thenReturn(700L);

		assertThat(resolver.resolveCustomerRoleTypeId()).isEqualTo(700L);
	}
}

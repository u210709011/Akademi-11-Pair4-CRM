package com.etiya.crm.customerservice.business.concretes;

import org.springframework.stereotype.Service;

import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;
import com.etiya.crm.shared.contracts.typevalue.TypeValueTables;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerLookupResolverImpl implements CustomerLookupResolver {

	private final LookupCacheService lookupCacheService;

	@Override
	public Long resolveCustomerDataTypeId() {
		return lookupCacheService.resolveDataTypeId(TypeValueTables.CUSTOMER);
	}

	@Override
	public Long resolveContactMediumTypeId(String shrtCode) {
		return lookupCacheService.resolveTypeId(GnlTpGroups.CONTACT_MEDIUM, shrtCode);
	}

	@Override
	public Long resolveActiveAccountStatusId() {
		return lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.ACTIVE);
	}

	@Override
	public Long resolvePassiveAccountStatusId() {
		return lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.PASSIVE);
	}

	@Override
	public Long resolveDeletedAccountStatusId() {
		return lookupCacheService.resolveStatusId(GnlStGroups.CUSTOMER_ACCOUNT, GnlStCodes.DELETED);
	}

	@Override
	public Long resolveCustomerAccountTypeId() {
		return lookupCacheService.resolveTypeId(GnlTpGroups.ACCOUNT_TYPE, GnlTpCodes.CUSTOMER_ACCOUNT);
	}

	@Override
	public Long resolveBillingAccountTypeId() {
		return lookupCacheService.resolveTypeId(GnlTpGroups.ACCOUNT_TYPE, GnlTpCodes.BILLING_ACCOUNT);
	}

	@Override
	public Long resolveIndividualCustomerTypeId() {
		return lookupCacheService.resolveTypeId(GnlTpGroups.CUSTOMER_TYPE, GnlTpCodes.INDIVIDUAL_CUSTOMER);
	}

	@Override
	public Long resolveCustomerRoleTypeId() {
		return lookupCacheService.resolveTypeId(GnlTpGroups.PARTY_ROLE_TYPE, GnlTpCodes.CUSTOMER_ROLE);
	}
}

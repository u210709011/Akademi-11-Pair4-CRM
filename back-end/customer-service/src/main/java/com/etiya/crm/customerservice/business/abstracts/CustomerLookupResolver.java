package com.etiya.crm.customerservice.business.abstracts;

/**
 * lookup-service'teki GNL_TP/GNL_ST/TYPE_VALUE ID'lerini domain'e anlamli
 * isimlerle cozer - cagiran taraf hangi group/code ciftine bakmasi gerektigini
 * bilmek zorunda kalmaz. Butun customer-service collaborator'lari (address,
 * contact, billing account, onboarding) ayni ID'lere ihtiyac duydugu icin
 * tek bir yerde toplanir.
 */
public interface CustomerLookupResolver {

	Long resolveCustomerDataTypeId();

	Long resolveContactMediumTypeId(String shrtCode);

	Long resolveActiveAccountStatusId();

	Long resolvePassiveAccountStatusId();

	Long resolveDeletedAccountStatusId();

	Long resolveCustomerAccountTypeId();

	Long resolveBillingAccountTypeId();

	Long resolveIndividualCustomerTypeId();
}

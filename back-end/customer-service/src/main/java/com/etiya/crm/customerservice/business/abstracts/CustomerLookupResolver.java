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

	/**
	 * Onboarding'de party-service'in her bireysel musteriye SABIT olarak atadigi rol tipi
	 * (bkz. party-service IndividualManager - GnlTpGroups.PARTY_ROLE_TYPE/GnlTpCodes.CUSTOMER_ROLE,
	 * hicbir zaman degismez). CustomerSearchView.role'u onboarding aninda senkron doldurmak icin -
	 * onceden bu alan SADECE asenkron PartyEventHandler (Kafka) ile dolduruluyordu, event
	 * kaybolursa/lookup-service cagrisi basarisiz olup DLQ'ya duserse role sonsuza kadar null
	 * kaliyordu (bkz. CustomerOnboardingServiceImpl.createSearchView).
	 */
	Long resolveCustomerRoleTypeId();
}

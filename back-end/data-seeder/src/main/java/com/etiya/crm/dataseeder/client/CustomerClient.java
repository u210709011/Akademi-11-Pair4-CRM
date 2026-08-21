package com.etiya.crm.dataseeder.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

/** customer-service'e onboarding + adres/hesap uc noktalari icin ince istemci. */
@Component
public class CustomerClient {

	private final GatewayClient gatewayClient;

	public CustomerClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
	}

	public CustomerResponse onboard(OnboardCustomerRequest request) {
		return gatewayClient.post("/api/v1/customers/onboarding", request, CustomerResponse.class);
	}

	public List<AddressResponse> getAddresses(Long custId) {
		return gatewayClient.get("/api/v1/customers/" + custId + "/addresses",
				new ParameterizedTypeReference<List<AddressResponse>>() {
				});
	}

	public AddressResponse addAddress(Long custId, AddressInfo address) {
		return gatewayClient.post("/api/v1/customers/" + custId + "/addresses", address, AddressResponse.class);
	}

	public CustomerAccountResponse createBillingAccount(Long custId, CreateBillingAccountRequest request) {
		return gatewayClient.post("/api/v1/customers/" + custId + "/accounts", request, CustomerAccountResponse.class);
	}

	public record IndividualInfo(String firstName, String middleName, String lastName, String birthDate,
			Long genderId, String motherName, String fatherName, String nationalId) {
	}

	public record AddressInfo(Long cityId, String streetName, String buildingName, String addressDesc) {
	}

	public record ContactInfo(String email, String mobilePhone, String homePhone, String fax) {
	}

	public record OnboardCustomerRequest(IndividualInfo individual, List<AddressInfo> addresses, ContactInfo contact) {
	}

	public record CreateBillingAccountRequest(String accountName, String accountDesc, Long addressId,
			AddressInfo newAddress) {
	}

	public record CustomerAccountResponse(Long custAcctId, String accountNo, String accountName, String accountDesc,
			Long accountTpId, Long addressId, Long acctStId, boolean active) {
	}

	public record CustomerResponse(Long custId, String custNo, Long partyRoleId, Long custTpId, boolean active,
			List<CustomerAccountResponse> accounts) {
	}

	public record AddressResponse(Long id, Long rowId, Long dataTypeId, Long cityId, String streetName,
			String houseName, String addrDesc, boolean primary) {
	}
}

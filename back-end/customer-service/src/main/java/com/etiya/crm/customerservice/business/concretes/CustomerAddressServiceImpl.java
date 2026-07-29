package com.etiya.crm.customerservice.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.rules.CustomerBusinessRules;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

	private final ContactAddressClient contactAddressClient;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerBusinessRules rules;
	private final CustomerLookupResolver lookupResolver;

	@Override
	@Transactional(readOnly = true)
	public List<AddressResponse> getAddresses(Long custId) {
		return contactAddressClient.getAddressesByCustomer(custId, lookupResolver.resolveCustomerDataTypeId());
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse addAddress(Long custId, AddressEditRequest request) {
		Long dataTypeId = lookupResolver.resolveCustomerDataTypeId();
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		rules.validateAddressLimit(existing.size());

		CreateAddressRequest command = new CreateAddressRequest(custId, dataTypeId, request.cityId(),
				request.streetName(), request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.addAddress(command);
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse updateAddress(Long custId, Long addressId, AddressEditRequest request) {
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId,
				lookupResolver.resolveCustomerDataTypeId());
		rules.ensureAddressBelongsToCustomer(custId, addressId, existing);

		UpdateAddressRequest command = new UpdateAddressRequest(request.cityId(), request.streetName(),
				request.buildingName(), request.addressDesc(), request.primary());
		return contactAddressClient.updateAddress(addressId, command);
	}

	@Override
	@Transactional(readOnly = true)
	public void deleteAddress(Long custId, Long addressId) {
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId,
				lookupResolver.resolveCustomerDataTypeId());
		AddressResponse address = rules.ensureAddressBelongsToCustomer(custId, addressId, existing);

		rules.ensureAddressNotPrimary(address);
		rules.ensureAddressNotLinkedToBillingAccount(customerAccountRepository
				.existsByAddressIdAndAcctStIdNotDeleted(addressId, lookupResolver.resolveDeletedAccountStatusId()));

		contactAddressClient.deleteAddress(addressId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long resolveBillingAddressId(Long custId, Long addressId, AddressInfo newAddress) {
		Long dataTypeId = lookupResolver.resolveCustomerDataTypeId();
		if (newAddress != null) {
			CreateAddressRequest command = new CreateAddressRequest(custId, dataTypeId, newAddress.cityId(),
					newAddress.streetName(), newAddress.buildingName(), newAddress.addressDesc(), false);
			return contactAddressClient.addAddress(command).id();
		}
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		rules.ensureAddressBelongsToCustomer(custId, addressId, existing);
		return addressId;
	}
}

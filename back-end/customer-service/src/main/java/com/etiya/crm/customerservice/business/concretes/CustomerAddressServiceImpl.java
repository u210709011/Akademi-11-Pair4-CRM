package com.etiya.crm.customerservice.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.rules.AddressBusinessRules;
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
	private final AddressBusinessRules rules;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerFinder customerFinder;

	@Override
	@Transactional(readOnly = true)
	public List<AddressResponse> getAddresses(Long custId) {
		customerFinder.getActiveCustomerOrThrow(custId);
		return contactAddressClient.getAddressesByCustomer(custId, lookupResolver.resolveCustomerDataTypeId());
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponse addAddress(Long custId, AddressEditRequest request) {
		customerFinder.getActiveCustomerOrThrow(custId);
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
		customerFinder.getActiveCustomerOrThrow(custId);
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
		customerFinder.getActiveCustomerOrThrow(custId);
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
	public AddressResponse resolveBillingAddress(Long custId, Long addressId, AddressInfo newAddress) {
		Long dataTypeId = lookupResolver.resolveCustomerDataTypeId();
		List<AddressResponse> existing = contactAddressClient.getAddressesByCustomer(custId, dataTypeId);
		if (newAddress != null) {
			// Onceden addAddress'teki gibi max-5 limiti kontrol edilmiyordu - billing account
			// create/update akisindan yeni adres eklenerek limit delinebiliyordu.
			rules.validateAddressLimit(existing.size());
			CreateAddressRequest command = new CreateAddressRequest(custId, dataTypeId, newAddress.cityId(),
					newAddress.streetName(), newAddress.buildingName(), newAddress.addressDesc(), false);
			return contactAddressClient.addAddress(command);
		}
		return rules.ensureAddressBelongsToCustomer(custId, addressId, existing);
	}
}

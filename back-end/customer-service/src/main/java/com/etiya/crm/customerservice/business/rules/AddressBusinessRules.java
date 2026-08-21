package com.etiya.crm.customerservice.business.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.customerservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.customerservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.PrimaryAddressCannotBeDeletedException;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.contactmedium.AddressCommand;

@Component
/** Adres ekleme ve silme kurallarını merkezi olarak uygular. */
public class AddressBusinessRules {

	private static final int MAX_ADDRESS_COUNT = 5;


	public List<AddressCommand> toAddressCommandsWithPrimaryRule(List<AddressInfo> addresses) {
		List<AddressCommand> commands = new ArrayList<>(addresses.size());
		for (int i = 0; i < addresses.size(); i++) {
			AddressInfo address = addresses.get(i);
			boolean primary = i == 0;
			commands.add(new AddressCommand(address.cityId(), address.streetName(), address.buildingName(),
					address.addressDesc(), primary));
		}
		return commands;
	}

	/** Müşteri başına adres sayısını sınırlar. */
	public void validateAddressLimit(long currentAddressCount) {
		if (currentAddressCount >= MAX_ADDRESS_COUNT) {
			throw new AddressLimitExceededException();
		}
	}

	/** Adresin ilgili müşteriye ait olduğunu doğrular. */
	public AddressResponse ensureAddressBelongsToCustomer(Long custId, Long addressId, List<AddressResponse> addresses) {
		return addresses.stream()
				.filter(address -> address.id().equals(addressId))
				.findFirst()
				.orElseThrow(() -> new AddressNotFoundException(custId, addressId));
	}

	/** Birincil adresin silinmesini engeller. */
	public void ensureAddressNotPrimary(AddressResponse address) {
		if (address.primary()) {
			throw new PrimaryAddressCannotBeDeletedException();
		}
	}

	/** Fatura hesabına bağlı adresin silinmesini engeller. */
	public void ensureAddressNotLinkedToBillingAccount(boolean linkedToBillingAccount) {
		if (linkedToBillingAccount) {
			throw new AddressLinkedToAccountException();
		}
	}
}

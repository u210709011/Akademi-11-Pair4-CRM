package com.etiya.crm.customerservice.business.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.customerservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.customerservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.customerservice.business.exceptions.InvalidCityException;
import com.etiya.crm.customerservice.business.exceptions.PrimaryAddressCannotBeDeletedException;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.contactmedium.AddressCommand;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;

import lombok.RequiredArgsConstructor;

/** contact-info-service'teki ADDR kayitlarina ozel FR-005/ACC-014..017 kurallari. */
@Component
@RequiredArgsConstructor
public class AddressBusinessRules {

	/** Musteri basina en fazla 5 adres olabilir (ADDRESS_MAX_EXCEEDED onboarding'de de kullanilir). */
	private static final int MAX_ADDRESS_COUNT = 5;

	private final LookupCacheService lookupCacheService;

	/**
	 * ACC-014..017: UI'da adres icin "primary" secimi yok; listedeki ilk adres
	 * server-side primary sayilir, digerleri primary=false gider.
	 */
	public List<AddressCommand> toAddressCommandsWithPrimaryRule(List<AddressInfo> addresses) {
		List<AddressCommand> commands = new ArrayList<>(addresses.size());
		for (int i = 0; i < addresses.size(); i++) {
			AddressInfo address = addresses.get(i);
			ensureCityExists(address.cityId());
			boolean primary = i == 0;
			commands.add(new AddressCommand(address.cityId(), address.streetName(), address.buildingName(),
					address.addressDesc(), primary));
		}
		return commands;
	}

	/**
	 * B-07/B-14: cityId onceden hicbir yerde dogrulanmiyordu - var olmayan/baska bir gruba ait
	 * bir id ile adres olusturulabiliyordu. Onboarding (toAddressCommandsWithPrimaryRule),
	 * "Add/Edit Address" ekrani ve billing account'un "yeni adres" akisi ayni tek metodu cagirir,
	 * boylece uc noktalar arttikca ayri ayri unutulma riski kalmaz.
	 */
	public void ensureCityExists(Long cityId) {
		if (!lookupCacheService.existsInGroup(cityId, GnlTpGroups.CITY)) {
			throw new InvalidCityException(cityId);
		}
	}

	/** Edit akisinda yeni adres eklenirken musteri basina max 5 sinirini uygular. */
	public void validateAddressLimit(long currentAddressCount) {
		if (currentAddressCount >= MAX_ADDRESS_COUNT) {
			throw new AddressLimitExceededException();
		}
	}

	/** IDOR onlemi: addressId, custId'nin kendi adresleri arasinda mi kontrol eder. */
	public AddressResponse ensureAddressBelongsToCustomer(Long custId, Long addressId, List<AddressResponse> addresses) {
		return addresses.stream()
				.filter(address -> address.id().equals(addressId))
				.findFirst()
				.orElseThrow(() -> new AddressNotFoundException(custId, addressId));
	}

	/** FR-005 ACC-009: birincil adres silinemez. */
	public void ensureAddressNotPrimary(AddressResponse address) {
		if (address.primary()) {
			throw new PrimaryAddressCannotBeDeletedException();
		}
	}

	/** FR-005 ACC-010/011: bir fatura hesabina bagli adres silinemez. */
	public void ensureAddressNotLinkedToBillingAccount(boolean linkedToBillingAccount) {
		if (linkedToBillingAccount) {
			throw new AddressLinkedToAccountException();
		}
	}
}

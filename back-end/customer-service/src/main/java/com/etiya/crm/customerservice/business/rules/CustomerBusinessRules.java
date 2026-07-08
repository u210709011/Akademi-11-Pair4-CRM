package com.etiya.crm.customerservice.business.rules;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.dtos.requests.AddressInfo;
import com.etiya.crm.customerservice.business.exceptions.DuplicateNationalIdException;
import com.etiya.crm.customerservice.business.exceptions.InvalidBirthDateException;
import com.etiya.crm.customerservice.clients.AddressCommand;

@Component
public class CustomerBusinessRules {

	private static final LocalDate MIN_BIRTH_DATE = LocalDate.of(1900, 1, 1);

	/** ACC-007: 01/01/1900 oncesi ya da bugunden sonraki tarihler gecersizdir. */
	public void validateBirthDate(LocalDate birthDate) {
		LocalDate today = LocalDate.now();
		if (birthDate.isBefore(MIN_BIRTH_DATE) || birthDate.isAfter(today)) {
			throw new InvalidBirthDateException();
		}
	}

	/** ACC-011/012: ayni Nationality ID mevcutsa ilerlenemez. */
	public void ensureUniqueNationalId(boolean nationalIdAlreadyExists) {
		if (nationalIdAlreadyExists) {
			throw new DuplicateNationalIdException();
		}
	}

	/**
	 * ACC-014..017: UI'da adres icin "primary" secimi yok; listedeki ilk adres
	 * server-side primary sayilir, digerleri primary=false gider.
	 */
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
}

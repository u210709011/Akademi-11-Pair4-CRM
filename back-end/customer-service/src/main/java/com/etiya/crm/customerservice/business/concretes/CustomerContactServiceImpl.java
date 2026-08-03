package com.etiya.crm.customerservice.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.etiya.crm.customerservice.business.abstracts.CustomerContactService;
import com.etiya.crm.customerservice.business.abstracts.CustomerFinder;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerContactServiceImpl implements CustomerContactService {

	private final ContactAddressClient contactAddressClient;
	private final CustomerLookupResolver lookupResolver;
	private final CustomerFinder customerFinder;

	@Override
	@Transactional(readOnly = true)
	public ContactInfo getContact(Long custId) {
		customerFinder.getActiveCustomerOrThrow(custId);
		List<ContactMediumResponse> mediums = contactAddressClient.getContactMediumsByCustomer(custId,
				lookupResolver.resolveCustomerDataTypeId());
		return new ContactInfo(
				findMediumValue(mediums, lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL)),
				findMediumValue(mediums, lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE)),
				findMediumValue(mediums, lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE)),
				findMediumValue(mediums, lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX)));
	}

	@Override
	@Transactional(readOnly = true)
	public ContactInfo updateContact(Long custId, ContactInfo request) {
		customerFinder.getActiveCustomerOrThrow(custId);
		Long dataTypeId = lookupResolver.resolveCustomerDataTypeId();
		List<ContactMediumResponse> existing = contactAddressClient.getContactMediumsByCustomer(custId, dataTypeId);

		ContactMediumResponse email = upsertMedium(custId, dataTypeId, existing,
				lookupResolver.resolveContactMediumTypeId(GnlTpCodes.EMAIL), request.email(), true);
		ContactMediumResponse mobile = upsertMedium(custId, dataTypeId, existing,
				lookupResolver.resolveContactMediumTypeId(GnlTpCodes.MOBILE), request.mobilePhone(), true);
		ContactMediumResponse home = upsertMedium(custId, dataTypeId, existing,
				lookupResolver.resolveContactMediumTypeId(GnlTpCodes.LANDLINE), request.homePhone(), false);
		ContactMediumResponse fax = upsertMedium(custId, dataTypeId, existing,
				lookupResolver.resolveContactMediumTypeId(GnlTpCodes.FAX), request.fax(), false);

		return new ContactInfo(email.cntcData(), mobile.cntcData(), home != null ? home.cntcData() : null,
				fax != null ? fax.cntcData() : null);
	}

	/**
	 * required=false alanlar (homePhone/fax) icin: mevcut kayit varsa ve yeni
	 * deger bossa dokunulmaz (silme desteklenmiyor); mevcut kayit yoksa ve deger
	 * de bossa hicbir sey yapilmaz (null doner).
	 */
	private ContactMediumResponse upsertMedium(Long custId, Long dataTypeId, List<ContactMediumResponse> existing,
			Long typeId, String value, boolean required) {
		ContactMediumResponse current = existing.stream()
				.filter(medium -> medium.cntcMediumTypeId().equals(typeId))
				.findFirst()
				.orElse(null);

		if (current != null) {
			if (!required && !StringUtils.hasText(value)) {
				return current;
			}
			UpdateContactMediumRequest command = new UpdateContactMediumRequest(value, typeId);
			return contactAddressClient.updateContactMedium(current.id(), command);
		}

		if (!StringUtils.hasText(value)) {
			return null;
		}

		CreateContactMediumRequest command = new CreateContactMediumRequest(custId, dataTypeId, value, typeId);
		return contactAddressClient.addContactMedium(command);
	}

	private String findMediumValue(List<ContactMediumResponse> mediums, Long typeId) {
		return mediums.stream()
				.filter(medium -> medium.cntcMediumTypeId().equals(typeId))
				.map(ContactMediumResponse::cntcData)
				.findFirst()
				.orElse(null);
	}
}

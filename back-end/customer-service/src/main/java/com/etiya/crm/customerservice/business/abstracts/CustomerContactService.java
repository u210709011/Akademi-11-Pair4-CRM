package com.etiya.crm.customerservice.business.abstracts;

import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;

/** Musterinin iletisim bilgisini (contact-info-service'in sahip oldugu CNTC_MEDIUM) yonetir. */
public interface CustomerContactService {

	ContactInfo getContact(Long custId);

	ContactInfo updateContact(Long custId, ContactInfo request);
}

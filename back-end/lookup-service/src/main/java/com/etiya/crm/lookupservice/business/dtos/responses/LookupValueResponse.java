package com.etiya.crm.lookupservice.business.dtos.responses;

/** customer-service/contact-info-service clients.LookupValueResponse ile birebir alan uyumu zorunlu. */
public record LookupValueResponse(Long id, String code, String value) {
}

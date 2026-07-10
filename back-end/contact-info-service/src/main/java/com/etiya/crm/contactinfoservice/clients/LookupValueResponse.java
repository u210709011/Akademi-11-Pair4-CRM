package com.etiya.crm.contactinfoservice.clients;

/** lookup-service'teki tek bir deger (id + is kodu + gosterim metni). */
public record LookupValueResponse(Long id, String code, String value) {
}

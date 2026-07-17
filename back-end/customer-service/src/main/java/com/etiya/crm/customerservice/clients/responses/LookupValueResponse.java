package com.etiya.crm.customerservice.clients.responses;

/** lookup-service'teki tek bir deger (id + is kodu + gosterim metni). */
public record LookupValueResponse(Long id, String code, String value) {
}

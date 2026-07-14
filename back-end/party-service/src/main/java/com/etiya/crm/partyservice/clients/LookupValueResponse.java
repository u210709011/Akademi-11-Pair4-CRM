package com.etiya.crm.partyservice.clients;

/** lookup-service'ten donen lookup degeri. */
public record LookupValueResponse(Long id, String code, String value) {
}
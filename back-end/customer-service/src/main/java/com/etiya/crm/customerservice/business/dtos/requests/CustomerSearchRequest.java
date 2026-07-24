package com.etiya.crm.customerservice.business.dtos.requests;

/**
 * firstName ve lastName ikisi de verilmisse AND, sadece biri verilmisse tek
 * basina uygulanir. tcNo/acctNo/custId/gsm diger alanlarla ve isim grubuyla
 * her zaman OR'lanir (bkz. CustomerSearchSpecifications).
 */
public record CustomerSearchRequest(String firstName, String lastName, String tcNo, String acctNo, Long custId,
		String gsm) {
}

package com.etiya.crm.customerservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/v1/general-types/resolve/{entCodeName}/{shrtCode}")
	GnlTpResponse resolveType(@PathVariable("entCodeName") String entCodeName,
			@PathVariable("shrtCode") String shrtCode);

	@GetMapping("/api/v1/general-types/{id}")
	GnlTpResponse getTypeById(@PathVariable("id") Long id);

	@GetMapping("/api/v1/general-statuses/resolve/{entCodeName}/{shrtCode}")
	GnlStResponse resolveStatus(@PathVariable("entCodeName") String entCodeName,
			@PathVariable("shrtCode") String shrtCode);

	// /resolve/{tableName} ucu yok - tum satirlari cekip bellekte tableName'e gore filtrelenir.
	@GetMapping("/api/v1/type-values")
	List<TypeValueResponse> getAllTypeValues();
}

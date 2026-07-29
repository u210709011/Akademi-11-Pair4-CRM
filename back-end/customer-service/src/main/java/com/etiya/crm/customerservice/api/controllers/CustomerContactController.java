package com.etiya.crm.customerservice.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerContactService;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.constants.Roles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin iletisim bilgisi (contact-info-service'e proxy) uc noktalari. */
@Tag(name = "Customers", description = "Musteri iletisim bilgisi yonetimi")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerContactController {

	private final CustomerContactService contactService;

	@Operation(summary = "Contact bilgisini getir", description = "contact-info-service'e proxy (musteri basina tek contact bilgisi).")
	@GetMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> getContact(@PathVariable Long custId) {
		return ResponseEntity.ok(contactService.getContact(custId));
	}

	@Operation(summary = "Contact bilgisini guncelle",
			description = "contact-info-service'e proxy. email/mobilePhone her zaman zorunlu; "
					+ "homePhone/fax bos gonderilirse mevcut kayit varsa dokunulmaz, yoksa olusturulmaz.")
	@PutMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> updateContact(@PathVariable Long custId,
			@Valid @RequestBody ContactInfo request) {
		return ResponseEntity.ok(contactService.updateContact(custId, request));
	}
}

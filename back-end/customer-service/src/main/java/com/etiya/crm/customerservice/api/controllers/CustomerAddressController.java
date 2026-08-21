package com.etiya.crm.customerservice.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerAddressService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.constants.Roles;
import com.etiya.crm.customerservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin adresleri (contact-info-service'e proxy) uc noktalari. */
@Tag(name = SwaggerText.CUSTOMER_TAG_NAME, description = SwaggerText.CUSTOMER_ADDRESS_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerAddressController {

	private final CustomerAddressService addressService;

	@Operation(summary = SwaggerText.GET_ADDRESSES_SUMMARY, description = SwaggerText.GET_ADDRESSES_DESCRIPTION)
	@GetMapping("/{custId}/addresses")
	public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long custId) {
		return ResponseEntity.ok(addressService.getAddresses(custId));
	}

	@Operation(summary = SwaggerText.ADD_ADDRESS_SUMMARY, description = SwaggerText.ADD_ADDRESS_DESCRIPTION)
	@PostMapping("/{custId}/addresses")
	public ResponseEntity<AddressResponse> addAddress(@PathVariable Long custId,
			@Valid @RequestBody AddressEditRequest request) {
		AddressResponse response = addressService.addAddress(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = SwaggerText.UPDATE_ADDRESS_SUMMARY, description = SwaggerText.UPDATE_ADDRESS_DESCRIPTION)
	@PutMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long custId,
			@Parameter(description = SwaggerText.UPDATE_ADDRESS_ADDRESS_ID_PARAM_DESCRIPTION) @PathVariable Long addressId,
			@Valid @RequestBody AddressEditRequest request) {
		return ResponseEntity.ok(addressService.updateAddress(custId, addressId, request));
	}

	@Operation(summary = SwaggerText.DELETE_ADDRESS_SUMMARY, description = SwaggerText.DELETE_ADDRESS_DESCRIPTION)
	@DeleteMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<Void> deleteAddress(@PathVariable Long custId,
			@Parameter(description = SwaggerText.DELETE_ADDRESS_ADDRESS_ID_PARAM_DESCRIPTION) @PathVariable Long addressId) {
		addressService.deleteAddress(custId, addressId);
		return ResponseEntity.noContent().build();
	}
}

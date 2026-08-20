package com.etiya.crm.customerservice.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerIndividualService;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.constants.Roles;
import com.etiya.crm.customerservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin kisisel bilgisi (party-service'e proxy) uc noktalari. */
@Tag(name = SwaggerText.CUSTOMER_TAG_NAME, description = SwaggerText.CUSTOMER_INDIVIDUAL_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerIndividualController {

	private final CustomerIndividualService individualService;

	@Operation(summary = SwaggerText.GET_INDIVIDUAL_SUMMARY, description = SwaggerText.GET_INDIVIDUAL_DESCRIPTION)
	@GetMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> getIndividual(@PathVariable Long custId) {
		return ResponseEntity.ok(individualService.getIndividual(custId));
	}

	@Operation(summary = SwaggerText.UPDATE_INDIVIDUAL_SUMMARY, description = SwaggerText.UPDATE_INDIVIDUAL_DESCRIPTION)
	@PutMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> updateIndividual(@PathVariable Long custId,
			@Valid @RequestBody UpdateIndividualInfo request) {
		return ResponseEntity.ok(individualService.updateIndividual(custId, request));
	}
}

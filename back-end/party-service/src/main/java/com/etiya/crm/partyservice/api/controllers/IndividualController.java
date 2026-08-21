package com.etiya.crm.partyservice.api.controllers;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.partyservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = SwaggerText.INDIVIDUAL_TAG_NAME, description = SwaggerText.INDIVIDUAL_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/individuals")
@RequiredArgsConstructor
public class IndividualController {

    private final IndividualService individualService;

    @Operation(summary = SwaggerText.CREATE_INDIVIDUAL_SUMMARY)
    @PostMapping
    public ResponseEntity<PartyRoleResponse> createIndividual(@Valid @RequestBody CreateIndividualCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(individualService.createIndividual(command));
    }

    @Operation(summary = SwaggerText.EXISTS_BY_NATIONAL_ID_SUMMARY)
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByNationalId(@RequestParam String nationalId) {
        return ResponseEntity.ok(individualService.existsByNationalId(nationalId));
    }

    @Operation(summary = SwaggerText.GET_BY_PARTY_ROLE_ID_SUMMARY)
    @GetMapping("/by-party-role/{partyRoleId}")
    public ResponseEntity<IndividualResponse> getByPartyRoleId(@PathVariable Long partyRoleId) {
        return ResponseEntity.ok(individualService.getByPartyRoleId(partyRoleId));
    }

    @Operation(summary = SwaggerText.UPDATE_BY_PARTY_ROLE_ID_SUMMARY, description = SwaggerText.UPDATE_BY_PARTY_ROLE_ID_DESCRIPTION)
    @PutMapping("/by-party-role/{partyRoleId}")
    public ResponseEntity<IndividualResponse> updateByPartyRoleId(@PathVariable Long partyRoleId,
            @Valid @RequestBody UpdateIndividualCommand command) {
        return ResponseEntity.ok(individualService.updateByPartyRoleId(partyRoleId, command));
    }
}

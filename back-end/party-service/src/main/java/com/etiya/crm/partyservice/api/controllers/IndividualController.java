package com.etiya.crm.partyservice.api.controllers;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.shared.contracts.individual.PartyRoleResponse;
import com.etiya.crm.shared.contracts.individual.UpdateIndividualCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/individuals")
@RequiredArgsConstructor
public class IndividualController {

    private final IndividualService individualService;

    @PostMapping
    public ResponseEntity<PartyRoleResponse> createIndividual(@Valid @RequestBody CreateIndividualCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(individualService.createIndividual(command));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByNationalId(@RequestParam String nationalId) {
        return ResponseEntity.ok(individualService.existsByNationalId(nationalId));
    }

    // customer-service PartyClient.getIndividualByPartyRoleId(...) buradan besleniyor -
    // Customer entity sadece partyRoleId tasidigi icin sorgu bu kimlikle yapilir.
    @GetMapping("/by-party-role/{partyRoleId}")
    public ResponseEntity<IndividualResponse> getByPartyRoleId(@PathVariable Long partyRoleId) {
        return ResponseEntity.ok(individualService.getByPartyRoleId(partyRoleId));
    }

    // customer-service PartyClient.updateIndividual(...) buradan besleniyor.
    // nationalId/birthDate govdede yok - editlenemez (bkz. UpdateIndividualCommand).
    @PutMapping("/by-party-role/{partyRoleId}")
    public ResponseEntity<IndividualResponse> updateByPartyRoleId(@PathVariable Long partyRoleId,
            @Valid @RequestBody UpdateIndividualCommand command) {
        return ResponseEntity.ok(individualService.updateByPartyRoleId(partyRoleId, command));
    }
}

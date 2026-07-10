package com.etiya.crm.partyservice.api.controllers;

import com.etiya.crm.partyservice.business.abstracts.IndividualService;
import com.etiya.crm.partyservice.business.dtos.requests.CreateIndividualCommand;
import com.etiya.crm.partyservice.business.dtos.responses.PartyRoleResponse;
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
}

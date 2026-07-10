package com.etiya.crm.partyservice.api.controllers;

import com.etiya.crm.partyservice.business.abstracts.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @DeleteMapping("/{partyId}")
    public ResponseEntity<Void> deleteParty(@PathVariable Long partyId) {
        partyService.softDeleteParty(partyId);
        return ResponseEntity.noContent().build();
    }
}

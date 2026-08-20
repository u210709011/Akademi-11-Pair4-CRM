package com.etiya.crm.partyservice.api.controllers;

import com.etiya.crm.partyservice.business.abstracts.PartyService;
import com.etiya.crm.partyservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = SwaggerText.PARTY_TAG_NAME, description = SwaggerText.PARTY_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @Operation(summary = SwaggerText.DELETE_PARTY_SUMMARY, description = SwaggerText.DELETE_PARTY_DESCRIPTION)
    @DeleteMapping("/{partyId}")
    public ResponseEntity<Void> deleteParty(@PathVariable Long partyId) {
        partyService.softDeleteParty(partyId);
        return ResponseEntity.noContent().build();
    }
}

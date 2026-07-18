package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.LookupService;
import com.etiya.crm.shared.contracts.lookup.LookupValueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Genel amacli id-deger tablosu. Ornek gruplar: GENDER, CITY, DATA_TYPE,
 * CNTC_MEDIUM_TYPE, CUST_STATUS, ACCT_STATUS, ACCT_TP, CUST_TP, PARTY_TYPE,
 * PARTY_ROLE_TYPE (bkz. lookup-service V2__seed_lookup.sql).
 */
@Tag(name = "Lookups", description = "Genel amacli id-deger tablosu (salt okunur)")
@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    /** valueId ":\\d+" ile kisitli; yoksa "/code/{code}" segmenti bu path'e cakisir. */
    @Operation(summary = "Deger id'si ile lookup degeri getir", description = "Ornek: GET /api/v1/lookups/CITY/201")
    @GetMapping("/{groupCode}/{valueId:\\d+}")
    public LookupValueResponse getByValueId(
            @Parameter(description = "Lookup grubu kodu", example = "CITY") @PathVariable String groupCode,
            @Parameter(description = "Grup icindeki deger id'si (VALUE_ID)", example = "201") @PathVariable Long valueId) {
        return lookupService.getByValueId(groupCode, valueId);
    }

    @Operation(summary = "Kod ile lookup degeri getir",
            description = "Bazi gruplarda (ornegin CITY) code alani null olabilir - o gruplar icin bu uc nokta kullanilamaz. "
                    + "Ornek: GET /api/v1/lookups/CNTC_MEDIUM_TYPE/code/EMAIL")
    @GetMapping("/{groupCode}/code/{code}")
    public LookupValueResponse getByCode(
            @Parameter(description = "Lookup grubu kodu", example = "CNTC_MEDIUM_TYPE") @PathVariable String groupCode,
            @Parameter(description = "Grup icindeki deger kodu (CODE)", example = "EMAIL") @PathVariable String code) {
        return lookupService.getByCode(groupCode, code);
    }
}

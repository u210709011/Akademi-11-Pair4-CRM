package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.LookupService;
import com.etiya.crm.lookupservice.business.dtos.responses.LookupValueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lookups")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    /** valueId ":\\d+" ile kisitli; yoksa "/code/{code}" segmenti bu path'e cakisir. */
    @GetMapping("/{groupCode}/{valueId:\\d+}")
    public LookupValueResponse getByValueId(@PathVariable String groupCode, @PathVariable Long valueId) {
        return lookupService.getByValueId(groupCode, valueId);
    }

    @GetMapping("/{groupCode}/code/{code}")
    public LookupValueResponse getByCode(@PathVariable String groupCode, @PathVariable String code) {
        return lookupService.getByCode(groupCode, code);
    }
}

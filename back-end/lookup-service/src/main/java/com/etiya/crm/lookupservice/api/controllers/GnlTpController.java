package com.etiya.crm.lookupservice.api.controllers;

import com.etiya.crm.lookupservice.business.abstracts.GnlTpService;
import com.etiya.crm.lookupservice.constants.SwaggerText;
import com.etiya.crm.shared.contracts.gnltp.CreateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = SwaggerText.GNL_TP_TAG_NAME, description = SwaggerText.GNL_TP_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/general-types")
@RequiredArgsConstructor
public class GnlTpController {

    private final GnlTpService gnlTpService;

    @Operation(summary = SwaggerText.GNL_TP_GET_ALL_SUMMARY, description = SwaggerText.GNL_TP_GET_ALL_DESCRIPTION)
    @GetMapping
    public ResponseEntity<List<GnlTpResponse>> getAll(@RequestParam(required = false) String entCodeName) {
        if (entCodeName != null) {
            return ResponseEntity.ok(gnlTpService.getAllByEntCodeName(entCodeName));
        }
        return ResponseEntity.ok(gnlTpService.getAll());
    }

    @Operation(summary = SwaggerText.GNL_TP_GET_BY_ID_SUMMARY)
    @GetMapping("/{id}")
    public ResponseEntity<GnlTpResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gnlTpService.getById(id));
    }

    @Operation(summary = SwaggerText.GNL_TP_RESOLVE_SUMMARY, description = SwaggerText.GNL_TP_RESOLVE_DESCRIPTION)
    @GetMapping("/resolve/{entCodeName}/{shrtCode}")
    public ResponseEntity<GnlTpResponse> getByEntCodeNameAndShrtCode(
            @PathVariable String entCodeName, @PathVariable String shrtCode) {
        return ResponseEntity.ok(gnlTpService.getByEntCodeNameAndShrtCode(entCodeName, shrtCode));
    }

    @Operation(summary = SwaggerText.GNL_TP_ADD_SUMMARY)
    @PostMapping
    public ResponseEntity<GnlTpResponse> add(@Valid @RequestBody CreateGnlTpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gnlTpService.add(request));
    }

    @Operation(summary = SwaggerText.GNL_TP_UPDATE_SUMMARY)
    @PutMapping("/{id}")
    public ResponseEntity<GnlTpResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateGnlTpRequest request) {
        return ResponseEntity.ok(gnlTpService.update(id, request));
    }

    @Operation(summary = SwaggerText.GNL_TP_DELETE_SUMMARY)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gnlTpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

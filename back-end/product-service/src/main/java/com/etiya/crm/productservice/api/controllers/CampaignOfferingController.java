package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.CampaignOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = SwaggerText.CAMPAIGN_OFFERING_TAG_NAME, description = SwaggerText.CAMPAIGN_OFFERING_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/campaign-offerings")
public class CampaignOfferingController {

    private final CampaignOfferingService campaignOfferingService;

    public CampaignOfferingController(CampaignOfferingService campaignOfferingService) {
        this.campaignOfferingService = campaignOfferingService;
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedCampaignOfferingResponse> create(@Valid @RequestBody CreateCampaignOfferingRequest request) {
        CreatedCampaignOfferingResponse response = campaignOfferingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_UPDATE_SUMMARY)
    @PutMapping("/{campaignOfferingId}")
    public ResponseEntity<UpdatedCampaignOfferingResponse> update(@PathVariable Long campaignOfferingId, @Valid @RequestBody UpdateCampaignOfferingRequest request) {
        UpdatedCampaignOfferingResponse response = campaignOfferingService.update(campaignOfferingId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_GET_BY_ID_SUMMARY)
    @GetMapping("/{campaignOfferingId}")
    public ResponseEntity<GetCampaignOfferingResponse> getById(@PathVariable Long campaignOfferingId) {
        GetCampaignOfferingResponse response = campaignOfferingService.getById(campaignOfferingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<List<GetAllCampaignOfferingResponse>> getAll() {
        List<GetAllCampaignOfferingResponse> response = campaignOfferingService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_GET_BY_CAMPAIGN_SUMMARY)
    @GetMapping("/by-campaign/{campaignId}")
    public ResponseEntity<List<GetAllCampaignOfferingResponse>> getByCampaignId(@PathVariable Long campaignId) {
        List<GetAllCampaignOfferingResponse> response = campaignOfferingService.getByCampaignId(campaignId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_OFFERING_DELETE_SUMMARY)
    @DeleteMapping("/{campaignOfferingId}")
    public ResponseEntity<Void> delete(@PathVariable Long campaignOfferingId) {
        campaignOfferingService.delete(campaignOfferingId);
        return ResponseEntity.noContent().build();
    }
}

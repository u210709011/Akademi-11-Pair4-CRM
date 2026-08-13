package com.etiya.crm.productservice.api.controllers;

import com.etiya.crm.productservice.business.abstracts.CampaignService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.constants.SwaggerText;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = SwaggerText.CAMPAIGN_TAG_NAME, description = SwaggerText.CAMPAIGN_TAG_DESCRIPTION)
@RestController
@RequestMapping("/api/v1/product-campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Operation(summary = SwaggerText.CAMPAIGN_CREATE_SUMMARY)
    @PostMapping
    public ResponseEntity<CreatedCampaignResponse> create(@Valid @RequestBody CreateCampaignRequest request){
        CreatedCampaignResponse response = campaignService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_UPDATE_SUMMARY)
    @PutMapping("/{campaignId}")
    public ResponseEntity<UpdatedCampaignResponse> update(@PathVariable Long campaignId, @Valid @RequestBody UpdateCampaignRequest request){
        UpdatedCampaignResponse response = campaignService.update(campaignId, request);
        return ResponseEntity.ok(response); // -> ResponseEntity.status(HttpStatus.OK).body(response) yazmanın aynısı
    }

    @Operation(summary = SwaggerText.CAMPAIGN_GET_BY_ID_SUMMARY)
    @GetMapping("/{campaignId}")
    public ResponseEntity<GetCampaignResponse> getById(@PathVariable Long campaignId){
        GetCampaignResponse response = campaignService.getById(campaignId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_GET_ALL_SUMMARY)
    @GetMapping
    public ResponseEntity<Page<GetAllCampaignResponse>> getAll(
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){
        Page<GetAllCampaignResponse> response = campaignService.getAll(campaignId, name, PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SwaggerText.CAMPAIGN_DELETE_SUMMARY)
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId){
        campaignService.delete(campaignId);
        return ResponseEntity.noContent().build(); // işlem başarılı ama dönecek veri yok
    }
}

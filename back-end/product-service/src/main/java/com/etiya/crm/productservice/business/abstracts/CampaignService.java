package com.etiya.crm.productservice.business.abstracts;

import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignService {
    CreatedCampaignResponse create(CreateCampaignRequest request);

    UpdatedCampaignResponse update(Long campaignId, UpdateCampaignRequest request);

    GetCampaignResponse getById(Long campaignId);

    Page<GetAllCampaignResponse> getAll(Long campaignId, String name, Pageable pageable);

    void delete(Long campaignId);
}

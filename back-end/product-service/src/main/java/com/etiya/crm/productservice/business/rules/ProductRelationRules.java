package com.etiya.crm.productservice.business.rules;

import org.springframework.stereotype.Component;

import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;

import lombok.RequiredArgsConstructor;

/**
 * ProductManager.create/update icinde aynen tekrarlanan iliski varliginin dogrulanmasi
 * (ProductOffering/ProductSpec/parentProduct/Campaign) buraya tasindi.
 */
@Component
@RequiredArgsConstructor
public class ProductRelationRules {

	private final ProductOfferingRepository productOfferingRepository;
	private final ProductSpecRepository productSpecRepository;
	private final ProductRepository productRepository;
	private final CampaignRepository campaignRepository;

	public ProductOffering getProductOffering(Long productOfferingId) {
		return productOfferingRepository.findById(productOfferingId)
				.orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));
	}

	public ProductSpec getProductSpec(Long productSpecId) {
		return productSpecRepository.findById(productSpecId)
				.orElseThrow(() -> new ProductSpecNotFoundException(productSpecId));
	}

	public Product getParentProduct(Long parentProductId) {
		return productRepository.findById(parentProductId)
				.orElseThrow(() -> new ProductNotFoundException(parentProductId));
	}

	public Campaign getCampaign(Long campaignId) {
		return campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
	}
}

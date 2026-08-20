package com.etiya.crm.productservice.business.rules;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRelationRulesTest {

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private ProductSpecRepository productSpecRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CampaignRepository campaignRepository;

	@InjectMocks
	private ProductRelationRules rules;

	@Test
	void getProductOffering_returnsEntity_whenFound() {
		ProductOffering offering = new ProductOffering();
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(offering));

		assertThat(rules.getProductOffering(1L)).isSameAs(offering);
	}

	@Test
	void getProductOffering_throws_whenMissing() {
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rules.getProductOffering(1L)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void getProductSpec_returnsEntity_whenFound() {
		ProductSpec spec = new ProductSpec();
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(spec));

		assertThat(rules.getProductSpec(1L)).isSameAs(spec);
	}

	@Test
	void getProductSpec_throws_whenMissing() {
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rules.getProductSpec(1L)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void getParentProduct_returnsEntity_whenFound() {
		Product product = new Product();
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		assertThat(rules.getParentProduct(1L)).isSameAs(product);
	}

	@Test
	void getParentProduct_throws_whenMissing() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rules.getParentProduct(1L)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void getCampaign_returnsEntity_whenFound() {
		Campaign campaign = new Campaign();
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

		assertThat(rules.getCampaign(1L)).isSameAs(campaign);
	}

	@Test
	void getCampaign_throws_whenMissing() {
		when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rules.getCampaign(1L)).isInstanceOf(CampaignNotFoundException.class);
	}

}

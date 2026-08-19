package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.rules.ProductRelationRules;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductMapper;
import com.etiya.crm.productservice.mapper.ProductMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductManagerTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductRelationRules productRelationRules;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductMapper productMapper = new ProductMapperImpl();

	private ProductManager manager() {
		return new ProductManager(productRepository, productRelationRules, productMapper, lookupCacheService);
	}

	private static ProductOffering offering(Long id, String name) {
		ProductOffering offering = new ProductOffering();
		offering.setProductOfferingId(id);
		offering.setName(name);
		return offering;
	}

	@Test
	void create_setsOfferingSpecAndStatus_withoutOptionalRelations() {
		CreateProductRequest request = new CreateProductRequest();
		request.setProductOfferingId(1L);
		request.setProductSpecId(2L);
		request.setStatusCode("ACTV");
		when(productRelationRules.getProductOffering(1L)).thenReturn(offering(1L, "Offer"));
		when(productRelationRules.getProductSpec(2L)).thenReturn(new ProductSpec());
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, "ACTV")).thenReturn(1L);
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(1L);
		verify(productRelationRules, never()).getParentProduct(any());
		verify(productRelationRules, never()).getCampaign(any());
	}

	@Test
	void create_setsOptionalRelations_whenIdsProvided() {
		CreateProductRequest request = new CreateProductRequest();
		request.setProductOfferingId(1L);
		request.setProductSpecId(2L);
		request.setStatusCode("ACTV");
		request.setParentProductId(3L);
		request.setCampaignId(4L);
		when(productRelationRules.getProductOffering(1L)).thenReturn(offering(1L, "Offer"));
		when(productRelationRules.getProductSpec(2L)).thenReturn(new ProductSpec());
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, "ACTV")).thenReturn(1L);
		when(productRelationRules.getParentProduct(3L)).thenReturn(new Product());
		when(productRelationRules.getCampaign(4L)).thenReturn(new Campaign());
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		manager().create(request);

		verify(productRelationRules).getParentProduct(3L);
		verify(productRelationRules).getCampaign(4L);
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductRequest request = new UpdateProductRequest();
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void update_clearsParentAndCampaign_whenIdsNull() {
		Product product = new Product();
		product.setProductId(1L);
		Product previousParent = new Product();
		product.setParentProduct(previousParent);
		product.setCampaign(new Campaign());
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		UpdateProductRequest request = new UpdateProductRequest();
		request.setProductOfferingId(1L);
		request.setProductSpecId(2L);
		request.setStatusCode("ACTV");
		when(productRelationRules.getProductOffering(1L)).thenReturn(offering(1L, "Offer"));
		when(productRelationRules.getProductSpec(2L)).thenReturn(new ProductSpec());
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, "ACTV")).thenReturn(1L);
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UpdatedProductResponse response = manager().update(1L, request);

		assertThat(product.getParentProduct()).isNull();
		assertThat(product.getCampaign()).isNull();
	}

	@Test
	void getById_throws_whenMissing() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void getById_setsOfferingName_andCampaignNameOnlyWhenPresent() {
		Product product = new Product();
		product.setProductId(1L);
		product.setProductOffering(offering(1L, "Offer"));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		GetProductResponse response = manager().getById(1L);

		assertThat(response.getProductOfferingName()).isEqualTo("Offer");
		assertThat(response.getCampaignName()).isNull();
	}

	@Test
	void getById_setsCampaignName_whenCampaignPresent() {
		Product product = new Product();
		product.setProductId(1L);
		product.setProductOffering(offering(1L, "Offer"));
		Campaign campaign = new Campaign();
		campaign.setName("Summer");
		product.setCampaign(campaign);
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		GetProductResponse response = manager().getById(1L);

		assertThat(response.getCampaignName()).isEqualTo("Summer");
	}

	@Test
	void getAll_enrichesEachResponse() {
		Product product = new Product();
		product.setProductId(1L);
		product.setProductOffering(offering(1L, "Offer"));
		when(productRepository.findAll()).thenReturn(List.of(product));

		assertThat(manager().getAll().get(0).getProductOfferingName()).isEqualTo("Offer");
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		Product product = new Product();
		product.setProductId(1L);
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT, GnlStCodes.DELETED)).thenReturn(9L);

		manager().delete(1L);

		assertThat(product.getStatusId()).isEqualTo(9L);
		verify(productRepository).save(product);
	}

}

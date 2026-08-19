package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.CreateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductRelation.UpdateProductRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductRelation.CreatedProductRelationResponse;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductRelationNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRelationRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductRelation;
import com.etiya.crm.productservice.mapper.ProductRelationMapper;
import com.etiya.crm.productservice.mapper.ProductRelationMapperImpl;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRelationManagerTest {

	@Mock
	private ProductRelationRepository productRelationRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductRelationMapper mapper = new ProductRelationMapperImpl();

	private ProductRelationManager manager() {
		return new ProductRelationManager(productRelationRepository, productRepository, mapper, lookupCacheService);
	}

	@Test
	void create_throws_whenFirstProductMissing() {
		CreateProductRelationRequest request = new CreateProductRelationRequest();
		request.setProductId1(1L);
		request.setProductId2(2L);
		request.setRelationTypeCode("PRNTPROD");
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void create_setsRelationsAndResolvesRelationType() {
		CreateProductRelationRequest request = new CreateProductRelationRequest();
		request.setProductId1(1L);
		request.setProductId2(2L);
		request.setRelationTypeCode("PRNTPROD");
		Product product1 = new Product();
		product1.setProductId(1L);
		Product product2 = new Product();
		product2.setProductId(2L);
		when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
		when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_REL, "PRNTPROD")).thenReturn(7L);
		when(productRelationRepository.save(any(ProductRelation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductRelationResponse response = manager().create(request);

		assertThat(response.getRelationTypeId()).isEqualTo(7L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productRelationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductRelationNotFoundException.class);
	}

	@Test
	void getAll_returnsMappedResponses() {
		ProductRelation relation = new ProductRelation();
		relation.setProductRelationId(1L);
		when(productRelationRepository.findAll()).thenReturn(List.of(relation));

		assertThat(manager().getAll()).hasSize(1);
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductRelationRequest request = new UpdateProductRelationRequest();
		when(productRelationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(ProductRelationNotFoundException.class);
	}

	@Test
	void delete_softDeletes() {
		ProductRelation relation = new ProductRelation();
		relation.setProductRelationId(1L);
		relation.setActive(true);
		when(productRelationRepository.findById(1L)).thenReturn(Optional.of(relation));

		manager().delete(1L);

		assertThat(relation.isActive()).isFalse();
		verify(productRelationRepository).save(relation);
	}

}

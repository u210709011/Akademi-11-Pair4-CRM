package com.etiya.crm.productservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecResourceSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecResourceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecResourceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecResourceSpecMapper;
import com.etiya.crm.productservice.mapper.ProductSpecResourceSpecMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecResourceSpecManagerTest {

	@Mock
	private ProductSpecResourceSpecRepository productSpecResourceSpecRepository;

	@Mock
	private ProductSpecRepository productSpecRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductSpecResourceSpecMapper mapper = new ProductSpecResourceSpecMapperImpl();

	private ProductSpecResourceSpecManager manager() {
		return new ProductSpecResourceSpecManager(productSpecResourceSpecRepository, productSpecRepository, mapper,
				lookupCacheService);
	}

	@Test
	void create_throws_whenProductSpecMissing() {
		CreateProductSpecResourceSpecRequest request = new CreateProductSpecResourceSpecRequest();
		request.setProductSpecId(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void create_setsStatusRelationTypeAndValidatesResourceSpec() {
		CreateProductSpecResourceSpecRequest request = new CreateProductSpecResourceSpecRequest();
		request.setProductSpecId(1L);
		request.setStatusCode("ACTV");
		request.setRelationTypeCode("REALIZED");
		request.setResourceSpecId(5L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_RESOURCE_SPEC, "ACTV")).thenReturn(1L);
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_RSRC_SPEC, "REALIZED")).thenReturn(2L);
		when(lookupCacheService.validateResourceSpecId(5L)).thenReturn(5L);
		when(productSpecResourceSpecRepository.save(any(ProductSpecResourceSpec.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductSpecResourceSpecResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(1L);
		assertThat(response.getRelationTypeId()).isEqualTo(2L);
		assertThat(response.getResourceSpecId()).isEqualTo(5L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productSpecResourceSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductSpecResourceSpecNotFoundException.class);
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductSpecResourceSpecRequest request = new UpdateProductSpecResourceSpecRequest();
		when(productSpecResourceSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request))
				.isInstanceOf(ProductSpecResourceSpecNotFoundException.class);
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductSpecResourceSpec entity = new ProductSpecResourceSpec();
		entity.setProductSpecResourceSpecId(1L);
		when(productSpecResourceSpecRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_RESOURCE_SPEC, GnlStCodes.DELETED))
				.thenReturn(9L);

		manager().delete(1L);

		assertThat(entity.getStatusId()).isEqualTo(9L);
		verify(productSpecResourceSpecRepository).save(entity);
	}

}

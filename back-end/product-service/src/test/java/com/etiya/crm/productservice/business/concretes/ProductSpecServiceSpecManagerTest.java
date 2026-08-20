package com.etiya.crm.productservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecServiceSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecServiceSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.entities.concretes.ProductSpecServiceSpec;
import com.etiya.crm.productservice.mapper.ProductSpecServiceSpecMapper;
import com.etiya.crm.productservice.mapper.ProductSpecServiceSpecMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecServiceSpecManagerTest {

	@Mock
	private ProductSpecServiceSpecRepository productSpecServiceSpecRepository;

	@Mock
	private ProductSpecRepository productSpecRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductSpecServiceSpecMapper mapper = new ProductSpecServiceSpecMapperImpl();

	private ProductSpecServiceSpecManager manager() {
		return new ProductSpecServiceSpecManager(productSpecServiceSpecRepository, productSpecRepository, mapper,
				lookupCacheService);
	}

	@Test
	void create_throws_whenProductSpecMissing() {
		CreateProductSpecServiceSpecRequest request = new CreateProductSpecServiceSpecRequest();
		request.setProductSpecId(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void create_setsStatusRelationTypeAndValidatesServiceSpec() {
		CreateProductSpecServiceSpecRequest request = new CreateProductSpecServiceSpecRequest();
		request.setProductSpecId(1L);
		request.setStatusCode("ACTV");
		request.setRelationTypeCode("REALIZED");
		request.setServiceSpecId(5L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_SERVICE_SPEC, "ACTV")).thenReturn(1L);
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_SPEC_SRVC_SPEC, "REALIZED")).thenReturn(2L);
		when(lookupCacheService.validateServiceSpecId(5L)).thenReturn(5L);
		when(productSpecServiceSpecRepository.save(any(ProductSpecServiceSpec.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductSpecServiceSpecResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(1L);
		assertThat(response.getRelationTypeId()).isEqualTo(2L);
		assertThat(response.getServiceSpecId()).isEqualTo(5L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productSpecServiceSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductSpecServiceSpecNotFoundException.class);
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductSpecServiceSpecRequest request = new UpdateProductSpecServiceSpecRequest();
		when(productSpecServiceSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request))
				.isInstanceOf(ProductSpecServiceSpecNotFoundException.class);
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductSpecServiceSpec entity = new ProductSpecServiceSpec();
		entity.setProductSpecServiceSpecId(1L);
		when(productSpecServiceSpecRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC_SERVICE_SPEC, GnlStCodes.DELETED))
				.thenReturn(9L);

		manager().delete(1L);

		assertThat(entity.getStatusId()).isEqualTo(9L);
		verify(productSpecServiceSpecRepository).save(entity);
	}

}

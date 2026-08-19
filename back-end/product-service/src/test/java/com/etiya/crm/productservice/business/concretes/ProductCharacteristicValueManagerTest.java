package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.CreateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.UpdateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.CreatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetAllProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.exceptions.ProductCharacteristicValueNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCharacteristicValueRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductRepository;
import com.etiya.crm.productservice.entities.concretes.Product;
import com.etiya.crm.productservice.entities.concretes.ProductCharacteristicValue;
import com.etiya.crm.productservice.mapper.ProductCharacteristicValueMapper;
import com.etiya.crm.productservice.mapper.ProductCharacteristicValueMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCharacteristicValueManagerTest {

	@Mock
	private ProductCharacteristicValueRepository productCharacteristicValueRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductCharacteristicValueMapper mapper = new ProductCharacteristicValueMapperImpl();

	private ProductCharacteristicValueManager manager() {
		return new ProductCharacteristicValueManager(productCharacteristicValueRepository, productRepository, mapper,
				lookupCacheService);
	}

	@Test
	void create_throws_whenProductMissing() {
		CreateProductCharacteristicValueRequest request = new CreateProductCharacteristicValueRequest();
		request.setProductId(1L);
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void create_validatesCharacteristicOnly_whenValueAndStatusNull() {
		CreateProductCharacteristicValueRequest request = new CreateProductCharacteristicValueRequest();
		request.setProductId(1L);
		request.setCharacteristicId(2L);
		when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
		when(lookupCacheService.validateCharacteristicId(2L)).thenReturn(2L);
		when(productCharacteristicValueRepository.save(any(ProductCharacteristicValue.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductCharacteristicValueResponse response = manager().create(request);

		assertThat(response.getCharacteristicId()).isEqualTo(2L);
		verify(lookupCacheService, org.mockito.Mockito.never()).validateCharacteristicValueId(any());
		verify(lookupCacheService, org.mockito.Mockito.never())
				.resolveStatusIdByCode(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
	}

	@Test
	void create_validatesCharacteristicValueAndStatus_whenProvided() {
		CreateProductCharacteristicValueRequest request = new CreateProductCharacteristicValueRequest();
		request.setProductId(1L);
		request.setCharacteristicId(2L);
		request.setCharacteristicValueId(3L);
		request.setStatusCode("ACTV");
		when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
		when(lookupCacheService.validateCharacteristicId(2L)).thenReturn(2L);
		when(lookupCacheService.validateCharacteristicValueId(3L)).thenReturn(3L);
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CHAR_VAL, "ACTV")).thenReturn(1L);
		when(productCharacteristicValueRepository.save(any(ProductCharacteristicValue.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductCharacteristicValueResponse response = manager().create(request);

		assertThat(response.getCharacteristicValueId()).isEqualTo(3L);
		assertThat(response.getStatusId()).isEqualTo(1L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productCharacteristicValueRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductCharacteristicValueNotFoundException.class);
	}

	@Test
	void getById_enrichesCharacteristicNameOnly_whenNoCharacteristicValueId() {
		ProductCharacteristicValue entity = new ProductCharacteristicValue();
		entity.setProductCharacteristicValueId(1L);
		entity.setCharacteristicId(2L);
		when(productCharacteristicValueRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(lookupCacheService.getCharacteristicName(2L)).thenReturn("Color");

		GetProductCharacteristicValueResponse response = manager().getById(1L);

		assertThat(response.getCharacteristicName()).isEqualTo("Color");
		assertThat(response.getCharacteristicValueName()).isNull();
	}

	@Test
	void getByProductId_throws_whenProductMissing() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getByProductId(1L)).isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void getAll_enrichesEachResponse() {
		ProductCharacteristicValue entity = new ProductCharacteristicValue();
		entity.setProductCharacteristicValueId(1L);
		entity.setCharacteristicId(2L);
		entity.setCharacteristicValueId(3L);
		when(productCharacteristicValueRepository.findAll()).thenReturn(List.of(entity));
		when(lookupCacheService.getCharacteristicName(2L)).thenReturn("Color");
		when(lookupCacheService.getCharacteristicValueName(3L)).thenReturn("Red");

		List<GetAllProductCharacteristicValueResponse> responses = manager().getAll();

		assertThat(responses.get(0).getCharacteristicName()).isEqualTo("Color");
		assertThat(responses.get(0).getCharacteristicValueName()).isEqualTo("Red");
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductCharacteristicValueRequest request = new UpdateProductCharacteristicValueRequest();
		when(productCharacteristicValueRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request))
				.isInstanceOf(ProductCharacteristicValueNotFoundException.class);
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductCharacteristicValue entity = new ProductCharacteristicValue();
		entity.setProductCharacteristicValueId(1L);
		when(productCharacteristicValueRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CHAR_VAL, GnlStCodes.DELETED))
				.thenReturn(9L);

		manager().delete(1L);

		assertThat(entity.getStatusId()).isEqualTo(9L);
		verify(productCharacteristicValueRepository).save(entity);
	}

}

package com.etiya.crm.productservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.CreatedProductOfferingCharUseResponse;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingCharUseDuplicateException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingCharUseNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingCharUseRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingCharUse;
import com.etiya.crm.productservice.mapper.ProductOfferingCharUseMapper;
import com.etiya.crm.productservice.mapper.ProductOfferingCharUseMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingCharUseManagerTest {

	@Mock
	private ProductOfferingCharUseRepository productOfferingCharUseRepository;

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductOfferingCharUseMapper mapper = new ProductOfferingCharUseMapperImpl();

	private ProductOfferingCharUseManager manager() {
		return new ProductOfferingCharUseManager(productOfferingCharUseRepository, productOfferingRepository, mapper,
				lookupCacheService);
	}

	@Test
	void create_throws_whenOfferingMissing() {
		CreateProductOfferingCharUseRequest request = new CreateProductOfferingCharUseRequest();
		request.setProductOfferingId(1L);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void create_throws_whenDuplicateCharacteristicForOffering() {
		CreateProductOfferingCharUseRequest request = new CreateProductOfferingCharUseRequest();
		request.setProductOfferingId(1L);
		request.setCharacteristicId(2L);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(new ProductOffering()));
		when(productOfferingCharUseRepository.existsByProductOffering_ProductOfferingIdAndCharacteristicId(1L, 2L))
				.thenReturn(true);

		assertThatThrownBy(() -> manager().create(request))
				.isInstanceOf(ProductOfferingCharUseDuplicateException.class);
	}

	@Test
	void create_savesAndEnrichesCharacteristicName() {
		CreateProductOfferingCharUseRequest request = new CreateProductOfferingCharUseRequest();
		request.setProductOfferingId(1L);
		request.setCharacteristicId(2L);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(new ProductOffering()));
		when(productOfferingCharUseRepository.existsByProductOffering_ProductOfferingIdAndCharacteristicId(1L, 2L))
				.thenReturn(false);
		when(productOfferingCharUseRepository.save(any(ProductOfferingCharUse.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(lookupCacheService.getCharacteristicName(2L)).thenReturn("Color");

		CreatedProductOfferingCharUseResponse response = manager().create(request);

		assertThat(response.getCharacteristicName()).isEqualTo("Color");
	}

	@Test
	void update_throws_whenDuplicateExcludingSelf() {
		UpdateProductOfferingCharUseRequest request = new UpdateProductOfferingCharUseRequest();
		request.setProductOfferingId(1L);
		request.setCharacteristicId(2L);
		ProductOfferingCharUse existing = new ProductOfferingCharUse();
		existing.setProductOfferingCharUseId(5L);
		when(productOfferingCharUseRepository.findById(5L)).thenReturn(Optional.of(existing));
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(new ProductOffering()));
		when(productOfferingCharUseRepository
				.existsByProductOffering_ProductOfferingIdAndCharacteristicIdAndProductOfferingCharUseIdNot(1L, 2L, 5L))
				.thenReturn(true);

		assertThatThrownBy(() -> manager().update(5L, request))
				.isInstanceOf(ProductOfferingCharUseDuplicateException.class);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productOfferingCharUseRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductOfferingCharUseNotFoundException.class);
	}

	@Test
	void getByProductOfferingId_throws_whenOfferingMissing() {
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getByProductOfferingId(1L))
				.isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void delete_softDeletes() {
		ProductOfferingCharUse entity = new ProductOfferingCharUse();
		entity.setProductOfferingCharUseId(1L);
		entity.setActive(true);
		when(productOfferingCharUseRepository.findById(1L)).thenReturn(Optional.of(entity));

		manager().delete(1L);

		assertThat(entity.isActive()).isFalse();
		verify(productOfferingCharUseRepository).save(entity);
	}

}

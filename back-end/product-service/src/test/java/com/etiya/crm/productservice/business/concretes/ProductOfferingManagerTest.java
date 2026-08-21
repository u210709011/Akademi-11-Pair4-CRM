package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductOfferingMapper;
import com.etiya.crm.productservice.mapper.ProductOfferingMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingManagerTest {

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private ProductSpecRepository productSpecRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private TranslationService translationService;

	private final ProductOfferingMapper mapper = new ProductOfferingMapperImpl();

	private ProductOfferingManager manager() {
		return new ProductOfferingManager(productOfferingRepository, productSpecRepository, mapper,
				lookupCacheService, translationService);
	}

	private static ProductOffering entity(Long id) {
		ProductOffering offering = new ProductOffering();
		offering.setProductOfferingId(id);
		offering.setName("Offer");
		offering.setDescr("desc");
		return offering;
	}

	@Test
	void create_throws_whenProductSpecMissing() {
		CreateProductOfferingRequest request = new CreateProductOfferingRequest();
		request.setProductSpecId(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void create_savesWithoutParentOffering_whenIdNull() {
		CreateProductOfferingRequest request = new CreateProductOfferingRequest();
		request.setProductSpecId(1L);
		request.setName("Offer");
		request.setDescr("desc");
		request.setStatusCode("ACTV");
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, "ACTV")).thenReturn(1L);
		when(productOfferingRepository.save(any(ProductOffering.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductOfferingResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(1L);
		verify(productOfferingRepository, org.mockito.Mockito.never()).findById(99L);
	}

	@Test
	void create_setsParentOffering_whenIdProvided() {
		CreateProductOfferingRequest request = new CreateProductOfferingRequest();
		request.setProductSpecId(1L);
		request.setStatusCode("ACTV");
		request.setParentOfferingId(2L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, "ACTV")).thenReturn(1L);
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.of(entity(2L)));
		when(productOfferingRepository.save(any(ProductOffering.class))).thenAnswer(invocation -> invocation.getArgument(0));

		manager().create(request);

		verify(productOfferingRepository).findById(2L);
	}

	@Test
	void create_throws_whenParentOfferingMissing() {
		CreateProductOfferingRequest request = new CreateProductOfferingRequest();
		request.setProductSpecId(1L);
		request.setStatusCode("ACTV");
		request.setParentOfferingId(2L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, "ACTV")).thenReturn(1L);
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void update_clearsParentOffering_whenIdNull() {
		ProductOffering offering = entity(1L);
		offering.setParentOffering(entity(2L));
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(offering));
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(new ProductSpec()));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, "ACTV")).thenReturn(1L);
		when(productOfferingRepository.save(any(ProductOffering.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateProductOfferingRequest request = new UpdateProductOfferingRequest();
		request.setProductSpecId(1L);
		request.setStatusCode("ACTV");

		UpdatedProductOfferingResponse response = manager().update(1L, request);

		assertThat(offering.getParentOffering()).isNull();
	}

	@Test
	void getAll_appliesTranslationToPagedResults() {
		Page<ProductOffering> page = new PageImpl<>(List.of(entity(1L)));
		Pageable pageable = PageRequest.of(0, 5);
		when(productOfferingRepository.findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
				.thenReturn(page);
		when(translationService.translate(anyString(), any(), anyString(), anyString()))
				.thenAnswer(invocation -> invocation.getArgument(3));

		Page<GetAllProductOfferingResponse> responses = manager().getAll(null, null, pageable);

		assertThat(responses.getContent()).hasSize(1);
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductOffering offering = entity(1L);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(offering));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_OFFER, GnlStCodes.DELETED)).thenReturn(9L);

		manager().delete(1L);

		assertThat(offering.getStatusId()).isEqualTo(9L);
		verify(productOfferingRepository).save(offering);
	}

}

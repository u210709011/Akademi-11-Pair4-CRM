package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingRelationNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRelationRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingRelation;
import com.etiya.crm.productservice.mapper.ProductOfferingRelationMapper;
import com.etiya.crm.productservice.mapper.ProductOfferingRelationMapperImpl;
import com.etiya.crm.shared.contracts.gnltp.GnlTpCodes;
import com.etiya.crm.shared.contracts.gnltp.GnlTpGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingRelationManagerTest {

	@Mock
	private ProductOfferingRelationRepository productOfferingRelationRepository;

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductOfferingRelationMapper mapper = new ProductOfferingRelationMapperImpl();

	private ProductOfferingRelationManager manager() {
		return new ProductOfferingRelationManager(productOfferingRelationRepository, productOfferingRepository,
				mapper, lookupCacheService);
	}

	@Test
	void create_throws_whenFirstOfferingMissing() {
		CreateProductOfferingRelationRequest request = new CreateProductOfferingRelationRequest();
		request.setProductOfferingId1(1L);
		request.setProductOfferingId2(2L);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void create_setsMandatoryFlag_whenRelationTypeCodeIsMandatory() {
		CreateProductOfferingRelationRequest request = new CreateProductOfferingRelationRequest();
		request.setProductOfferingId1(1L);
		request.setProductOfferingId2(2L);
		request.setRelationTypeCode(GnlTpCodes.MANDATORY);
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(new ProductOffering()));
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.of(new ProductOffering()));
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.MANDATORY)).thenReturn(1L);
		when(productOfferingRelationRepository.save(any(ProductOfferingRelation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductOfferingRelationResponse response = manager().create(request);

		assertThat(response.getMandatory()).isTrue();
		assertThat(response.getExclusive()).isFalse();
	}

	@Test
	void getById_throws_whenMissing() {
		when(productOfferingRelationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductOfferingRelationNotFoundException.class);
	}

	@Test
	void getById_setsMandatoryTrue_whenRelationTypeIdMatchesMandatory() {
		ProductOfferingRelation entity = new ProductOfferingRelation();
		entity.setProductOfferingRelationId(1L);
		entity.setRelationTypeId(5L);
		when(productOfferingRelationRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.MANDATORY)).thenReturn(5L);
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.EXCL)).thenReturn(6L);

		GetProductOfferingRelationResponse response = manager().getById(1L);

		assertThat(response.getMandatory()).isTrue();
		assertThat(response.getExclusive()).isFalse();
	}

	@Test
	void getByProductOfferingId_throws_whenOfferingMissing() {
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getByProductOfferingId(1L))
				.isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void getByProductOfferingId_returnsActiveRelationsWithFlags() {
		when(productOfferingRepository.findById(1L)).thenReturn(Optional.of(new ProductOffering()));
		ProductOfferingRelation relation = new ProductOfferingRelation();
		relation.setProductOfferingRelationId(1L);
		relation.setRelationTypeId(6L);
		when(productOfferingRelationRepository.findByProductOffering1_ProductOfferingIdAndActiveTrue(1L))
				.thenReturn(List.of(relation));
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.MANDATORY)).thenReturn(5L);
		when(lookupCacheService.resolveTypeIdByCode(GnlTpGroups.PROD_OFR_REL, GnlTpCodes.EXCL)).thenReturn(6L);

		var responses = manager().getByProductOfferingId(1L);

		assertThat(responses.get(0).getExclusive()).isTrue();
	}

	@Test
	void delete_softDeletes() {
		ProductOfferingRelation entity = new ProductOfferingRelation();
		entity.setProductOfferingRelationId(1L);
		entity.setActive(true);
		when(productOfferingRelationRepository.findById(1L)).thenReturn(Optional.of(entity));

		manager().delete(1L);

		assertThat(entity.isActive()).isFalse();
		verify(productOfferingRelationRepository).save(entity);
	}

}

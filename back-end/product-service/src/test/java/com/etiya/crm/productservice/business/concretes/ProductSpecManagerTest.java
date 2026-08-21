package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.CreateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpec.UpdateProductSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.CreatedProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetAllProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.GetProductSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpec.UpdatedProductSpecResponse;
import com.etiya.crm.productservice.business.exceptions.ProductSpecNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductSpecRepository;
import com.etiya.crm.productservice.entities.concretes.ProductSpec;
import com.etiya.crm.productservice.mapper.ProductSpecMapper;
import com.etiya.crm.productservice.mapper.ProductSpecMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecManagerTest {

	@Mock
	private ProductSpecRepository productSpecRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private TranslationService translationService;

	private final ProductSpecMapper productSpecMapper = new ProductSpecMapperImpl();

	private ProductSpecManager manager() {
		return new ProductSpecManager(productSpecRepository, productSpecMapper, lookupCacheService, translationService);
	}

	private static ProductSpec entity(Long id) {
		ProductSpec spec = new ProductSpec();
		spec.setProductSpecId(id);
		spec.setName("SIM Spec");
		spec.setDescr("desc");
		spec.setDev(false);
		return spec;
	}

	@Test
	void create_resolvesStatusAndSaves() {
		CreateProductSpecRequest request = new CreateProductSpecRequest("SIM Spec", "desc", "ACTV", false);
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC, "ACTV")).thenReturn(1L);
		when(productSpecRepository.save(any(ProductSpec.class))).thenAnswer(invocation -> {
			ProductSpec saved = invocation.getArgument(0);
			saved.setProductSpecId(1L);
			return saved;
		});

		CreatedProductSpecResponse response = manager().create(request);

		assertThat(response.getProductSpecId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("SIM Spec");
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductSpecRequest request = new UpdateProductSpecRequest("New", "desc", "ACTV", false);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void update_overwritesFieldsAndResolvesStatus() {
		ProductSpec spec = entity(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(spec));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC, "PASS")).thenReturn(2L);
		when(productSpecRepository.save(any(ProductSpec.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateProductSpecRequest request = new UpdateProductSpecRequest("Updated", "desc2", "PASS", true);

		UpdatedProductSpecResponse response = manager().update(1L, request);

		assertThat(response.getName()).isEqualTo("Updated");
		assertThat(response.getStatusId()).isEqualTo(2L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productSpecRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductSpecNotFoundException.class);
	}

	@Test
	void getById_appliesTranslation() {
		ProductSpec spec = entity(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(spec));
		when(translationService.translate("PROD_SPEC", 1L, "NAME", "SIM Spec")).thenReturn("SIM Ozellik");
		when(translationService.translate("PROD_SPEC", 1L, "DESCR", "desc")).thenReturn("aciklama");

		GetProductSpecResponse response = manager().getById(1L);

		assertThat(response.getName()).isEqualTo("SIM Ozellik");
	}

	@Test
	void getAll_appliesTranslationToEachItem() {
		when(productSpecRepository.findAll()).thenReturn(List.of(entity(1L)));
		when(translationService.translate(anyString(), any(), anyString(), anyString()))
				.thenAnswer(invocation -> invocation.getArgument(3));

		List<GetAllProductSpecResponse> responses = manager().getAll();

		assertThat(responses).hasSize(1);
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductSpec spec = entity(1L);
		when(productSpecRepository.findById(1L)).thenReturn(Optional.of(spec));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_SPEC, GnlStCodes.DELETED)).thenReturn(9L);

		manager().delete(1L);

		assertThat(spec.getStatusId()).isEqualTo(9L);
		verify(productSpecRepository).save(spec);
	}

}

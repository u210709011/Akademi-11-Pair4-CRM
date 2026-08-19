package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;
import com.etiya.crm.productservice.business.exceptions.ProductCatalogNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.mapper.ProductCatalogMapper;
import com.etiya.crm.productservice.mapper.ProductCatalogMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogManagerTest {

	@Mock
	private ProductCatalogRepository productCatalogRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private TranslationService translationService;

	private final ProductCatalogMapper productCatalogMapper = new ProductCatalogMapperImpl();

	private ProductCatalogManager manager() {
		return new ProductCatalogManager(productCatalogRepository, productCatalogMapper, lookupCacheService,
				translationService);
	}

	private static ProductCatalog entity(Long id) {
		ProductCatalog catalog = new ProductCatalog();
		catalog.setProductCatalogId(id);
		catalog.setName("Mobile Catalog");
		catalog.setDescr("desc");
		catalog.setShortCode("MOB");
		return catalog;
	}

	@Test
	void create_resolvesStatusAndSaves() {
		CreateProductCatalogRequest request = new CreateProductCatalogRequest("Mobile Catalog", "desc", "ACTV", "MOB");
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, "ACTV")).thenReturn(1L);
		when(productCatalogRepository.save(any(ProductCatalog.class))).thenAnswer(invocation -> {
			ProductCatalog saved = invocation.getArgument(0);
			saved.setProductCatalogId(1L);
			return saved;
		});

		CreatedProductCatalogResponse response = manager().create(request);

		assertThat(response.getProductCatalogId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Mobile Catalog");
	}

	@Test
	void update_throws_whenMissing() {
		UpdateProductCatalogRequest request = new UpdateProductCatalogRequest("New Name", "desc", "ACTV", "MOB");
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(ProductCatalogNotFoundException.class);
	}

	@Test
	void update_overwritesFieldsAndResolvesStatus() {
		ProductCatalog catalog = entity(1L);
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, "PASS")).thenReturn(2L);
		when(productCatalogRepository.save(any(ProductCatalog.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateProductCatalogRequest request = new UpdateProductCatalogRequest("Updated", "desc2", "PASS", "MOB2");

		UpdatedProductCatalogResponse response = manager().update(1L, request);

		assertThat(response.getName()).isEqualTo("Updated");
		assertThat(response.getStatusId()).isEqualTo(2L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductCatalogNotFoundException.class);
	}

	@Test
	void getById_appliesTranslation() {
		ProductCatalog catalog = entity(1L);
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
		when(translationService.translate("PROD_CATAL", 1L, "NAME", "Mobile Catalog")).thenReturn("Mobil Katalog");
		when(translationService.translate("PROD_CATAL", 1L, "DESCR", "desc")).thenReturn("aciklama");

		GetProductCatalogResponse response = manager().getById(1L);

		assertThat(response.getName()).isEqualTo("Mobil Katalog");
		assertThat(response.getDescr()).isEqualTo("aciklama");
	}

	@Test
	void getAll_appliesTranslationToEachItem() {
		when(productCatalogRepository.findAll()).thenReturn(List.of(entity(1L)));
		when(translationService.translate(org.mockito.ArgumentMatchers.anyString(), any(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
				.thenAnswer(invocation -> invocation.getArgument(3));

		List<GetAllProductCatalogResponse> responses = manager().getAll();

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getName()).isEqualTo("Mobile Catalog");
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductCatalog catalog = entity(1L);
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG, GnlStCodes.DELETED)).thenReturn(9L);

		manager().delete(1L);

		assertThat(catalog.getStatusId()).isEqualTo(9L);
		verify(productCatalogRepository).save(catalog);
	}

}

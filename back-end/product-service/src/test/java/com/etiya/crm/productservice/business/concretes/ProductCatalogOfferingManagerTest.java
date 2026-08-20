package com.etiya.crm.productservice.business.concretes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.exceptions.ProductCatalogNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductCatalogOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductCatalogRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import com.etiya.crm.productservice.entities.concretes.ProductCatalogOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.mapper.ProductCatalogOfferingMapper;
import com.etiya.crm.productservice.mapper.ProductCatalogOfferingMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogOfferingManagerTest {

	@Mock
	private ProductCatalogOfferingRepository productCatalogOfferingRepository;

	@Mock
	private ProductCatalogRepository productCatalogRepository;

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	private final ProductCatalogOfferingMapper mapper = new ProductCatalogOfferingMapperImpl();

	private ProductCatalogOfferingManager manager() {
		return new ProductCatalogOfferingManager(productCatalogOfferingRepository, productCatalogRepository,
				productOfferingRepository, mapper, lookupCacheService);
	}

	private static ProductOffering offering(Long id, String name, String price) {
		ProductOffering offering = new ProductOffering();
		offering.setProductOfferingId(id);
		offering.setName(name);
		offering.setTotalPrice(new BigDecimal(price));
		return offering;
	}

	@Test
	void create_throws_whenCatalogMissing() {
		CreateProductCatalogOfferingRequest request = new CreateProductCatalogOfferingRequest();
		request.setProductCatalogId(1L);
		request.setProductOfferingId(2L);
		request.setStatusCode("ACTV");
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductCatalogNotFoundException.class);
	}

	@Test
	void create_throws_whenOfferingMissing() {
		CreateProductCatalogOfferingRequest request = new CreateProductCatalogOfferingRequest();
		request.setProductCatalogId(1L);
		request.setProductOfferingId(2L);
		request.setStatusCode("ACTV");
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(new ProductCatalog()));
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void create_setsRelationsAndStatus() {
		CreateProductCatalogOfferingRequest request = new CreateProductCatalogOfferingRequest();
		request.setProductCatalogId(1L);
		request.setProductOfferingId(2L);
		request.setStatusCode("ACTV");
		ProductCatalog catalog = new ProductCatalog();
		catalog.setProductCatalogId(1L);
		ProductOffering offering = offering(2L, "Offer", "100.00");
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.of(offering));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG_OFFER, "ACTV")).thenReturn(5L);
		when(productCatalogOfferingRepository.save(any(ProductCatalogOffering.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		CreatedProductCatalogOfferingResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(5L);
	}

	@Test
	void getById_throws_whenMissing() {
		when(productCatalogOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(ProductCatalogOfferingNotFoundException.class);
	}

	@Test
	void getByCatalogId_throws_whenCatalogMissing() {
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getByCatalogId(1L)).isInstanceOf(ProductCatalogNotFoundException.class);
	}

	@Test
	void getByCatalogId_setsOfferingNameAndPriceFromEntity() {
		ProductCatalog catalog = new ProductCatalog();
		catalog.setProductCatalogId(1L);
		when(productCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));

		ProductOffering offering = offering(2L, "Offer", "150.00");
		ProductCatalogOffering pco = new ProductCatalogOffering();
		pco.setProductCatalogOfferingId(10L);
		pco.setProductCatalog(catalog);
		pco.setProductOffering(offering);
		when(productCatalogOfferingRepository
				.findByProductCatalog_ProductCatalogIdOrderByProductOffering_TotalPriceAsc(1L))
				.thenReturn(List.of(pco));

		List<GetAllProductCatalogOfferingResponse> responses = manager().getByCatalogId(1L);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getProductOfferingName()).isEqualTo("Offer");
		assertThat(responses.get(0).getTotalPrice()).isEqualByComparingTo("150.00");
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		ProductCatalogOffering pco = new ProductCatalogOffering();
		pco.setProductCatalogOfferingId(1L);
		when(productCatalogOfferingRepository.findById(1L)).thenReturn(Optional.of(pco));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.PRODUCT_CATALOG_OFFER, GnlStCodes.DELETED))
				.thenReturn(9L);

		manager().delete(1L);

		assertThat(pco.getStatusId()).isEqualTo(9L);
		verify(productCatalogOfferingRepository).save(pco);
	}

	@Test
	void update_throws_whenNotFound() {
		UpdateProductCatalogOfferingRequest request = new UpdateProductCatalogOfferingRequest();
		when(productCatalogOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request))
				.isInstanceOf(ProductCatalogOfferingNotFoundException.class);
	}

}

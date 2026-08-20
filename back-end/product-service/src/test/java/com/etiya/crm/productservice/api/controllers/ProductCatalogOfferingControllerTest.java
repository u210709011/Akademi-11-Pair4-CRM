package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductCatalogOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.CreateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalogOffering.UpdateProductCatalogOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.CreatedProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetAllProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.GetProductCatalogOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalogOffering.UpdatedProductCatalogOfferingResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogOfferingControllerTest {

	@Mock
	private ProductCatalogOfferingService productCatalogOfferingService;

	@InjectMocks
	private ProductCatalogOfferingController controller;

	@Test
	void create_returns201() {
		CreateProductCatalogOfferingRequest request = new CreateProductCatalogOfferingRequest();
		CreatedProductCatalogOfferingResponse response = new CreatedProductCatalogOfferingResponse();
		when(productCatalogOfferingService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductCatalogOfferingRequest request = new UpdateProductCatalogOfferingRequest();
		when(productCatalogOfferingService.update(1L, request)).thenReturn(new UpdatedProductCatalogOfferingResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsOffering() {
		GetProductCatalogOfferingResponse response = new GetProductCatalogOfferingResponse();
		when(productCatalogOfferingService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductCatalogOfferingResponse> list = List.of(new GetAllProductCatalogOfferingResponse());
		when(productCatalogOfferingService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void getByCatalogId_returnsList() {
		List<GetAllProductCatalogOfferingResponse> list = List.of(new GetAllProductCatalogOfferingResponse());
		when(productCatalogOfferingService.getByCatalogId(1L)).thenReturn(list);

		assertThat(controller.getByCatalogId(1L).getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productCatalogOfferingService).delete(1L);
	}

}

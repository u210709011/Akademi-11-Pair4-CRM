package com.etiya.crm.productservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.CreateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOffering.UpdateProductOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.CreatedProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetAllProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.GetProductOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOffering.UpdatedProductOfferingResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingControllerTest {

	@Mock
	private ProductOfferingService productOfferingService;

	@InjectMocks
	private ProductOfferingController controller;

	@Test
	void create_returns201() {
		CreateProductOfferingRequest request = new CreateProductOfferingRequest();
		CreatedProductOfferingResponse response = new CreatedProductOfferingResponse();
		when(productOfferingService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductOfferingRequest request = new UpdateProductOfferingRequest();
		when(productOfferingService.update(1L, request)).thenReturn(new UpdatedProductOfferingResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsOffering() {
		GetProductOfferingResponse response = new GetProductOfferingResponse();
		when(productOfferingService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_delegatesWithPageRequest() {
		Page<GetAllProductOfferingResponse> page = new PageImpl<>(java.util.List.of());
		when(productOfferingService.getAll(null, null, PageRequest.of(0, 5))).thenReturn(page);

		ResponseEntity<Page<GetAllProductOfferingResponse>> result = controller.getAll(null, null, 0, 5);

		assertThat(result.getBody()).isSameAs(page);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productOfferingService).delete(1L);
	}

}

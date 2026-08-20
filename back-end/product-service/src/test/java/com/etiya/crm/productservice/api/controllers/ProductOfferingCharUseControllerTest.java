package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingCharUseService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.CreatedProductOfferingCharUseResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.GetAllProductOfferingCharUseResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.GetProductOfferingCharUseResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.UpdatedProductOfferingCharUseResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingCharUseControllerTest {

	@Mock
	private ProductOfferingCharUseService productOfferingCharUseService;

	@InjectMocks
	private ProductOfferingCharUseController controller;

	@Test
	void create_returns201() {
		CreateProductOfferingCharUseRequest request = new CreateProductOfferingCharUseRequest();
		CreatedProductOfferingCharUseResponse response = new CreatedProductOfferingCharUseResponse();
		when(productOfferingCharUseService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductOfferingCharUseRequest request = new UpdateProductOfferingCharUseRequest();
		when(productOfferingCharUseService.update(1L, request)).thenReturn(new UpdatedProductOfferingCharUseResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsCharUse() {
		GetProductOfferingCharUseResponse response = new GetProductOfferingCharUseResponse();
		when(productOfferingCharUseService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductOfferingCharUseResponse> list = List.of(new GetAllProductOfferingCharUseResponse());
		when(productOfferingCharUseService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void getByProductOfferingId_returnsList() {
		List<GetAllProductOfferingCharUseResponse> list = List.of(new GetAllProductOfferingCharUseResponse());
		when(productOfferingCharUseService.getByProductOfferingId(1L)).thenReturn(list);

		assertThat(controller.getByProductOfferingId(1L).getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productOfferingCharUseService).delete(1L);
	}

}

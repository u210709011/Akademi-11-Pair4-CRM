package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductSpecResourceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.CreateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecResourceSpec.UpdateProductSpecResourceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.CreatedProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetAllProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.GetProductSpecResourceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecResourceSpec.UpdatedProductSpecResourceSpecResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecResourceSpecControllerTest {

	@Mock
	private ProductSpecResourceSpecService productSpecResourceSpecService;

	@InjectMocks
	private ProductSpecResourceSpecController controller;

	@Test
	void create_returns201() {
		CreateProductSpecResourceSpecRequest request = new CreateProductSpecResourceSpecRequest();
		CreatedProductSpecResourceSpecResponse response = new CreatedProductSpecResourceSpecResponse();
		when(productSpecResourceSpecService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductSpecResourceSpecRequest request = new UpdateProductSpecResourceSpecRequest();
		when(productSpecResourceSpecService.update(1L, request))
				.thenReturn(new UpdatedProductSpecResourceSpecResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsRelation() {
		GetProductSpecResourceSpecResponse response = new GetProductSpecResourceSpecResponse();
		when(productSpecResourceSpecService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductSpecResourceSpecResponse> list = List.of(new GetAllProductSpecResourceSpecResponse());
		when(productSpecResourceSpecService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productSpecResourceSpecService).delete(1L);
	}

}

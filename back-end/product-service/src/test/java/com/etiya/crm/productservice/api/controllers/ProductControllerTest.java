package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductService;
import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductController controller;

	@Test
	void create_returns201() {
		CreateProductRequest request = new CreateProductRequest();
		CreatedProductResponse response = new CreatedProductResponse();
		when(productService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductRequest request = new UpdateProductRequest();
		when(productService.update(1L, request)).thenReturn(new UpdatedProductResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsProduct() {
		GetProductResponse response = new GetProductResponse();
		when(productService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductResponse> list = List.of(new GetAllProductResponse());
		when(productService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productService).delete(1L);
	}

}

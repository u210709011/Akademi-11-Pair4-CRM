package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductSpecServiceSpecService;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.CreateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductSpecServiceSpec.UpdateProductSpecServiceSpecRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.CreatedProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetAllProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.GetProductSpecServiceSpecResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductSpecServiceSpec.UpdatedProductSpecServiceSpecResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSpecServiceSpecControllerTest {

	@Mock
	private ProductSpecServiceSpecService productSpecServiceSpecService;

	@InjectMocks
	private ProductSpecServiceSpecController controller;

	@Test
	void create_returns201() {
		CreateProductSpecServiceSpecRequest request = new CreateProductSpecServiceSpecRequest();
		CreatedProductSpecServiceSpecResponse response = new CreatedProductSpecServiceSpecResponse();
		when(productSpecServiceSpecService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductSpecServiceSpecRequest request = new UpdateProductSpecServiceSpecRequest();
		when(productSpecServiceSpecService.update(1L, request))
				.thenReturn(new UpdatedProductSpecServiceSpecResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsRelation() {
		GetProductSpecServiceSpecResponse response = new GetProductSpecServiceSpecResponse();
		when(productSpecServiceSpecService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductSpecServiceSpecResponse> list = List.of(new GetAllProductSpecServiceSpecResponse());
		when(productSpecServiceSpecService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productSpecServiceSpecService).delete(1L);
	}

}

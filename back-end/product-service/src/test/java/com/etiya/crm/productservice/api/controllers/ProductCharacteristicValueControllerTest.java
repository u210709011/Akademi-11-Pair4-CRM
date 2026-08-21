package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductCharacteristicValueService;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.CreateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCharacteristicValue.UpdateProductCharacteristicValueRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.CreatedProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetAllProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.GetProductCharacteristicValueResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCharacteristicValue.UpdatedProductCharacteristicValueResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCharacteristicValueControllerTest {

	@Mock
	private ProductCharacteristicValueService productCharacteristicValueService;

	@InjectMocks
	private ProductCharacteristicValueController controller;

	@Test
	void create_returns201() {
		CreateProductCharacteristicValueRequest request = new CreateProductCharacteristicValueRequest();
		CreatedProductCharacteristicValueResponse response = new CreatedProductCharacteristicValueResponse();
		when(productCharacteristicValueService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductCharacteristicValueRequest request = new UpdateProductCharacteristicValueRequest();
		when(productCharacteristicValueService.update(1L, request))
				.thenReturn(new UpdatedProductCharacteristicValueResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsValue() {
		GetProductCharacteristicValueResponse response = new GetProductCharacteristicValueResponse();
		when(productCharacteristicValueService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductCharacteristicValueResponse> list = List.of(new GetAllProductCharacteristicValueResponse());
		when(productCharacteristicValueService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void getByProductId_returnsList() {
		List<GetAllProductCharacteristicValueResponse> list = List.of(new GetAllProductCharacteristicValueResponse());
		when(productCharacteristicValueService.getByProductId(1L)).thenReturn(list);

		assertThat(controller.getByProductId(1L).getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productCharacteristicValueService).delete(1L);
	}

}

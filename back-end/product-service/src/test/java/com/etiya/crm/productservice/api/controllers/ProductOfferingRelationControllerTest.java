package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.ProductOfferingRelationService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.CreateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingRelation.UpdateProductOfferingRelationRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.CreatedProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetAllProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.GetProductOfferingRelationResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingRelation.UpdatedProductOfferingRelationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOfferingRelationControllerTest {

	@Mock
	private ProductOfferingRelationService productOfferingRelationService;

	@InjectMocks
	private ProductOfferingRelationController controller;

	@Test
	void create_returns201() {
		CreateProductOfferingRelationRequest request = new CreateProductOfferingRelationRequest();
		CreatedProductOfferingRelationResponse response = new CreatedProductOfferingRelationResponse();
		when(productOfferingRelationService.create(request)).thenReturn(response);

		assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateProductOfferingRelationRequest request = new UpdateProductOfferingRelationRequest();
		when(productOfferingRelationService.update(1L, request))
				.thenReturn(new UpdatedProductOfferingRelationResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsRelation() {
		GetProductOfferingRelationResponse response = new GetProductOfferingRelationResponse();
		when(productOfferingRelationService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllProductOfferingRelationResponse> list = List.of(new GetAllProductOfferingRelationResponse());
		when(productOfferingRelationService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void getByProductOfferingId_returnsList() {
		List<GetAllProductOfferingRelationResponse> list = List.of(new GetAllProductOfferingRelationResponse());
		when(productOfferingRelationService.getByProductOfferingId(1L)).thenReturn(list);

		assertThat(controller.getByProductOfferingId(1L).getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productOfferingRelationService).delete(1L);
	}

}

package com.etiya.crm.productservice.api.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.productservice.business.abstracts.CampaignOfferingService;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.UpdateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.UpdatedCampaignOfferingResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignOfferingControllerTest {

	@Mock
	private CampaignOfferingService campaignOfferingService;

	@InjectMocks
	private CampaignOfferingController controller;

	@Test
	void create_returns201() {
		CreateCampaignOfferingRequest request = new CreateCampaignOfferingRequest();
		CreatedCampaignOfferingResponse response = new CreatedCampaignOfferingResponse();
		when(campaignOfferingService.create(request)).thenReturn(response);

		ResponseEntity<CreatedCampaignOfferingResponse> result = controller.create(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void update_returns200() {
		UpdateCampaignOfferingRequest request = new UpdateCampaignOfferingRequest();
		when(campaignOfferingService.update(1L, request)).thenReturn(new UpdatedCampaignOfferingResponse());

		assertThat(controller.update(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsOffering() {
		GetCampaignOfferingResponse response = new GetCampaignOfferingResponse();
		when(campaignOfferingService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_returnsList() {
		List<GetAllCampaignOfferingResponse> list = List.of(new GetAllCampaignOfferingResponse());
		when(campaignOfferingService.getAll()).thenReturn(list);

		assertThat(controller.getAll().getBody()).isSameAs(list);
	}

	@Test
	void getByCampaignId_returnsList() {
		List<GetAllCampaignOfferingResponse> list = List.of(new GetAllCampaignOfferingResponse());
		when(campaignOfferingService.getByCampaignId(1L)).thenReturn(list);

		assertThat(controller.getByCampaignId(1L).getBody()).isSameAs(list);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(campaignOfferingService).delete(1L);
	}

}

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

import com.etiya.crm.productservice.business.abstracts.CampaignService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.UpdatedCampaignResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

	@Mock
	private CampaignService campaignService;

	@InjectMocks
	private CampaignController controller;

	@Test
	void create_returns201() {
		CreateCampaignRequest request = new CreateCampaignRequest();
		CreatedCampaignResponse response = new CreatedCampaignResponse();
		when(campaignService.create(request)).thenReturn(response);

		ResponseEntity<CreatedCampaignResponse> result = controller.create(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isSameAs(response);
	}

	@Test
	void update_returns200() {
		UpdateCampaignRequest request = new UpdateCampaignRequest();
		UpdatedCampaignResponse response = new UpdatedCampaignResponse();
		when(campaignService.update(1L, request)).thenReturn(response);

		ResponseEntity<UpdatedCampaignResponse> result = controller.update(1L, request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getById_returnsCampaign() {
		GetCampaignResponse response = new GetCampaignResponse();
		when(campaignService.getById(1L)).thenReturn(response);

		assertThat(controller.getById(1L).getBody()).isSameAs(response);
	}

	@Test
	void getAll_delegatesWithPageRequest() {
		Page<GetAllCampaignResponse> page = new PageImpl<>(java.util.List.of());
		when(campaignService.getAll(null, null, PageRequest.of(0, 5))).thenReturn(page);

		ResponseEntity<Page<GetAllCampaignResponse>> result = controller.getAll(null, null, 0, 5);

		assertThat(result.getBody()).isSameAs(page);
	}

	@Test
	void delete_returns204AndDelegates() {
		ResponseEntity<Void> result = controller.delete(1L);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(campaignService).delete(1L);
	}

}

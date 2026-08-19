package com.etiya.crm.productservice.business.concretes;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.CreateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.requests.Campaign.UpdateCampaignRequest;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.CreatedCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetAllCampaignResponse;
import com.etiya.crm.productservice.business.dtos.responses.Campaign.GetCampaignResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.mapper.CampaignMapper;
import com.etiya.crm.productservice.mapper.CampaignMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignManagerTest {

	@Mock
	private CampaignRepository campaignRepository;

	@Mock
	private LookupCacheService lookupCacheService;

	@Mock
	private TranslationService translationService;

	private final CampaignMapper campaignMapper = new CampaignMapperImpl();

	private CampaignManager manager() {
		return new CampaignManager(campaignRepository, campaignMapper, lookupCacheService, translationService);
	}

	private static Campaign entity(Long id) {
		Campaign campaign = new Campaign();
		campaign.setCampaignId(id);
		campaign.setName("Summer Campaign");
		campaign.setDescr("desc");
		campaign.setCampaignCode("SUMMER");
		campaign.setPenalty(false);
		return campaign;
	}

	@Test
	void create_resolvesStatusAndSaves() {
		CreateCampaignRequest request = new CreateCampaignRequest();
		request.setName("Summer Campaign");
		request.setDescr("desc");
		request.setStatusCode("ACTV");
		request.setCampaignCode("SUMMER");
		request.setPenalty(false);
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.CAMPAIGN, "ACTV")).thenReturn(1L);
		when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreatedCampaignResponse response = manager().create(request);

		assertThat(response.getStatusId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Summer Campaign");
	}

	@Test
	void update_throws_whenMissing() {
		UpdateCampaignRequest request = new UpdateCampaignRequest();
		when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(CampaignNotFoundException.class);
	}

	@Test
	void getById_throws_whenMissing() {
		when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(CampaignNotFoundException.class);
	}

	@Test
	void getById_appliesTranslation() {
		Campaign campaign = entity(1L);
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
		when(translationService.translate("CMPG", 1L, "NAME", "Summer Campaign")).thenReturn("Yaz Kampanyasi");
		when(translationService.translate("CMPG", 1L, "DESCR", "desc")).thenReturn("aciklama");

		GetCampaignResponse response = manager().getById(1L);

		assertThat(response.getName()).isEqualTo("Yaz Kampanyasi");
	}

	@Test
	void getAll_appliesTranslationToPagedResults() {
		Page<Campaign> page = new PageImpl<>(List.of(entity(1L)));
		Pageable pageable = PageRequest.of(0, 5);
		when(campaignRepository.findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
				.thenReturn(page);
		when(translationService.translate(org.mockito.ArgumentMatchers.anyString(), any(),
				org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
				.thenAnswer(invocation -> invocation.getArgument(3));

		Page<GetAllCampaignResponse> responses = manager().getAll(null, null, pageable);

		assertThat(responses.getContent()).hasSize(1);
		assertThat(responses.getContent().get(0).getName()).isEqualTo("Summer Campaign");
	}

	@Test
	void delete_resolvesDeletedStatusAndSaves() {
		Campaign campaign = entity(1L);
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
		when(lookupCacheService.resolveStatusIdByCode(GnlStGroups.CAMPAIGN, GnlStCodes.DELETED)).thenReturn(9L);

		manager().delete(1L);

		assertThat(campaign.getStatusId()).isEqualTo(9L);
		verify(campaignRepository).save(campaign);
	}

}

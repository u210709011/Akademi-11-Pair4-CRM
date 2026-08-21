package com.etiya.crm.productservice.business.concretes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.productservice.business.abstracts.TranslationService;
import com.etiya.crm.productservice.business.dtos.requests.CampaignOffering.CreateCampaignOfferingRequest;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.CreatedCampaignOfferingResponse;
import com.etiya.crm.productservice.business.dtos.responses.CampaignOffering.GetAllCampaignOfferingResponse;
import com.etiya.crm.productservice.business.exceptions.CampaignNotFoundException;
import com.etiya.crm.productservice.business.exceptions.CampaignOfferingNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignOfferingRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.CampaignRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.Campaign;
import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.mapper.CampaignOfferingMapper;
import com.etiya.crm.productservice.mapper.CampaignOfferingMapperImpl;
import com.etiya.crm.productservice.utils.DiscountPriceCalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignOfferingManagerTest {

	@Mock
	private CampaignOfferingRepository campaignOfferingRepository;

	@Mock
	private CampaignRepository campaignRepository;

	@Mock
	private ProductOfferingRepository productOfferingRepository;

	@Mock
	private TranslationService translationService;

	private final CampaignOfferingMapper mapper = new CampaignOfferingMapperImpl();
	private final DiscountPriceCalculator discountPriceCalculator = new DiscountPriceCalculator();

	private CampaignOfferingManager manager() {
		return new CampaignOfferingManager(campaignOfferingRepository, campaignRepository, productOfferingRepository,
				mapper, discountPriceCalculator, translationService);
	}

	private static ProductOffering offering(Long id, String name, String price) {
		ProductOffering offering = new ProductOffering();
		offering.setProductOfferingId(id);
		offering.setName(name);
		offering.setTotalPrice(new BigDecimal(price));
		return offering;
	}

	@Test
	void create_throws_whenCampaignMissing() {
		CreateCampaignOfferingRequest request = new CreateCampaignOfferingRequest();
		request.setCampaignId(1L);
		when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(CampaignNotFoundException.class);
	}

	@Test
	void create_throws_whenOfferingMissing() {
		CreateCampaignOfferingRequest request = new CreateCampaignOfferingRequest();
		request.setCampaignId(1L);
		request.setProductOfferingId(2L);
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(new Campaign()));
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().create(request)).isInstanceOf(ProductOfferingNotFoundException.class);
	}

	@Test
	void create_snapshotsOfferingNameAndComputesDiscountedPrice() {
		CreateCampaignOfferingRequest request = new CreateCampaignOfferingRequest();
		request.setCampaignId(1L);
		request.setProductOfferingId(2L);
		request.setDiscountPct(new BigDecimal("10"));
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(new Campaign()));
		ProductOffering offering = offering(2L, "Offer", "100.00");
		when(productOfferingRepository.findById(2L)).thenReturn(Optional.of(offering));
		when(campaignOfferingRepository.save(any(CampaignOffering.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreatedCampaignOfferingResponse response = manager().create(request);

		assertThat(response.getProductOfferingName()).isEqualTo("Offer");
		assertThat(response.getDiscountedPrice()).isEqualByComparingTo("90.00");
	}

	@Test
	void getById_throws_whenMissing() {
		when(campaignOfferingRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(CampaignOfferingNotFoundException.class);
	}

	@Test
	void getByCampaignId_filtersInactiveAndExpiredOfferings() {
		when(campaignRepository.findById(1L)).thenReturn(Optional.of(new Campaign()));

		CampaignOffering active = campaignOffering(1L, true, null);
		CampaignOffering expired = campaignOffering(2L, true, LocalDate.now().minusDays(1));
		CampaignOffering inactive = campaignOffering(3L, false, null);
		when(campaignOfferingRepository.findByCampaign_CampaignId(1L)).thenReturn(List.of(active, expired, inactive));
		when(translationService.translate(anyString(), any(), anyString(), anyString()))
				.thenAnswer(invocation -> invocation.getArgument(3));

		List<GetAllCampaignOfferingResponse> responses = manager().getByCampaignId(1L);

		assertThat(responses).hasSize(1);
	}

	@Test
	void delete_softDeletes() {
		CampaignOffering campaignOffering = new CampaignOffering();
		campaignOffering.setCampaignOfferingId(1L);
		campaignOffering.setActive(true);
		when(campaignOfferingRepository.findById(1L)).thenReturn(Optional.of(campaignOffering));

		manager().delete(1L);

		assertThat(campaignOffering.isActive()).isFalse();
		verify(campaignOfferingRepository).save(campaignOffering);
	}

	private static CampaignOffering campaignOffering(Long id, boolean active, LocalDate endDate) {
		CampaignOffering co = new CampaignOffering();
		co.setCampaignOfferingId(id);
		co.setActive(active);
		co.setEndDate(endDate);
		co.setDiscountPct(BigDecimal.TEN);
		co.setProductOfferingName("Offer" + id);
		co.setProductOffering(offering(100L + id, "Offer" + id, "100.00"));
		return co;
	}

}

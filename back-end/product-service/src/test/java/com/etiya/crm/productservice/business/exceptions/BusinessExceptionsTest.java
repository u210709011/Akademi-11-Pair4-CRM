package com.etiya.crm.productservice.business.exceptions;

import org.junit.jupiter.api.Test;

import com.etiya.crm.productservice.constants.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionsTest {

	@Test
	void campaignNotFound_carriesCampaignId() {
		CampaignNotFoundException ex = new CampaignNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CAMPAIGN_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void campaignOfferingNotFound_carriesId() {
		CampaignOfferingNotFoundException ex = new CampaignOfferingNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CAMPAIGN_OFFERING_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void characteristicNotFound_carriesId() {
		CharacteristicNotFoundException ex = new CharacteristicNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CHARACTERISTIC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void characteristicValueNotFound_carriesId() {
		CharacteristicValueNotFoundException ex = new CharacteristicValueNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.CHARACTERISTIC_VALUE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void lookupValueNotFound_carriesEntCodeNameAndShrtCode() {
		LookupValueNotFoundException ex = new LookupValueNotFoundException("PROD", "ACTV");
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.LOOKUP_VALUE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly("PROD", "ACTV");
	}

	@Test
	void productCatalogNotFound_carriesId() {
		ProductCatalogNotFoundException ex = new ProductCatalogNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_CATALOG_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productCatalogOfferingNotFound_carriesId() {
		ProductCatalogOfferingNotFoundException ex = new ProductCatalogOfferingNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_CATALOG_OFFERING_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productCharacteristicValueNotFound_carriesId() {
		ProductCharacteristicValueNotFoundException ex = new ProductCharacteristicValueNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_CHARACTERISTIC_VALUE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productNotFound_carriesId() {
		ProductNotFoundException ex = new ProductNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productOfferingCharUseDuplicate_carriesOfferingIdAndCharId() {
		ProductOfferingCharUseDuplicateException ex = new ProductOfferingCharUseDuplicateException(1L, 2L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_OFFERING_CHAR_USE_DUPLICATE);
		assertThat(ex.getArgs()).containsExactly(1L, 2L);
	}

	@Test
	void productOfferingCharUseNotFound_carriesId() {
		ProductOfferingCharUseNotFoundException ex = new ProductOfferingCharUseNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_OFFERING_CHAR_USE_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productOfferingNotFound_carriesId() {
		ProductOfferingNotFoundException ex = new ProductOfferingNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_OFFERING_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productOfferingRelationNotFound_carriesId() {
		ProductOfferingRelationNotFoundException ex = new ProductOfferingRelationNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_OFFERING_RELATION_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productRelationNotFound_carriesId() {
		ProductRelationNotFoundException ex = new ProductRelationNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_RELATION_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productSpecNotFound_carriesId() {
		ProductSpecNotFoundException ex = new ProductSpecNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productSpecResourceSpecNotFound_carriesId() {
		ProductSpecResourceSpecNotFoundException ex = new ProductSpecResourceSpecNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_SPEC_RESOURCE_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void productSpecServiceSpecNotFound_carriesId() {
		ProductSpecServiceSpecNotFoundException ex = new ProductSpecServiceSpecNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.PRODUCT_SPEC_SERVICE_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void resourceSpecNotFound_carriesId() {
		ResourceSpecNotFoundException ex = new ResourceSpecNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.RESOURCE_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

	@Test
	void serviceSpecNotFound_carriesId() {
		ServiceSpecNotFoundException ex = new ServiceSpecNotFoundException(1L);
		assertThat(ex.getMessageKey()).isEqualTo(MessageKeys.SERVICE_SPEC_NOT_FOUND);
		assertThat(ex.getArgs()).containsExactly(1L);
	}

}

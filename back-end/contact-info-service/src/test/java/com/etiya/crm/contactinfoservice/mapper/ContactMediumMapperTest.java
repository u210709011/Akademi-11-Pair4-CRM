package com.etiya.crm.contactinfoservice.mapper;

import org.junit.jupiter.api.Test;

import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ContactMediumMapperTest {

	@Test
	void toEntity_mapsAllFieldsFromRequest() {
		CreateContactMediumRequest request = new CreateContactMediumRequest(42L, 12L, "user@example.com", 4001L);

		ContactMedium contactMedium = ContactMediumMapper.toEntity(request);

		assertThat(contactMedium.getRowId()).isEqualTo(42L);
		assertThat(contactMedium.getDataTypeId()).isEqualTo(12L);
		assertThat(contactMedium.getCntcData()).isEqualTo("user@example.com");
		assertThat(contactMedium.getCntcMediumTypeId()).isEqualTo(4001L);
	}

	@Test
	void updateEntity_overwritesMutableFields_butNotRowIdOrDataTypeId() {
		ContactMedium contactMedium = new ContactMedium();
		contactMedium.setId(1L);
		contactMedium.setRowId(42L);
		contactMedium.setDataTypeId(12L);
		UpdateContactMediumRequest request = new UpdateContactMediumRequest("user2@example.com", 4002L);

		ContactMediumMapper.updateEntity(contactMedium, request);

		assertThat(contactMedium.getCntcData()).isEqualTo("user2@example.com");
		assertThat(contactMedium.getCntcMediumTypeId()).isEqualTo(4002L);
		assertThat(contactMedium.getRowId()).isEqualTo(42L);
		assertThat(contactMedium.getDataTypeId()).isEqualTo(12L);
	}

	@Test
	void toResponse_mapsAllFields() {
		ContactMedium contactMedium = new ContactMedium();
		contactMedium.setId(1L);
		contactMedium.setRowId(42L);
		contactMedium.setDataTypeId(12L);
		contactMedium.setCntcData("user@example.com");
		contactMedium.setCntcMediumTypeId(4001L);

		ContactMediumResponse response = ContactMediumMapper.toResponse(contactMedium);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.rowId()).isEqualTo(42L);
		assertThat(response.dataTypeId()).isEqualTo(12L);
		assertThat(response.cntcData()).isEqualTo("user@example.com");
		assertThat(response.cntcMediumTypeId()).isEqualTo(4001L);
	}

}

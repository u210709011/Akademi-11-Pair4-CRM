package com.etiya.crm.contactinfoservice.mapper;

import org.junit.jupiter.api.Test;

import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.address.UpdateAddressRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

	@Test
	void toEntity_mapsAllFieldsFromRequest() {
		CreateAddressRequest request = new CreateAddressRequest(42L, 12L, 5L, "Street", "12", "Desc", true);

		Address address = AddressMapper.toEntity(request);

		assertThat(address.getRowId()).isEqualTo(42L);
		assertThat(address.getDataTypeId()).isEqualTo(12L);
		assertThat(address.getCityId()).isEqualTo(5L);
		assertThat(address.getStreetName()).isEqualTo("Street");
		assertThat(address.getHouseName()).isEqualTo("12");
		assertThat(address.getAddrDesc()).isEqualTo("Desc");
		assertThat(address.isPrimary()).isTrue();
	}

	@Test
	void updateEntity_overwritesMutableFields_butNotRowIdOrDataTypeId() {
		Address address = new Address();
		address.setId(1L);
		address.setRowId(42L);
		address.setDataTypeId(12L);
		UpdateAddressRequest request = new UpdateAddressRequest(6L, "NewStreet", "13", "NewDesc", false);

		AddressMapper.updateEntity(address, request);

		assertThat(address.getCityId()).isEqualTo(6L);
		assertThat(address.getStreetName()).isEqualTo("NewStreet");
		assertThat(address.getHouseName()).isEqualTo("13");
		assertThat(address.getAddrDesc()).isEqualTo("NewDesc");
		assertThat(address.isPrimary()).isFalse();
		assertThat(address.getRowId()).isEqualTo(42L);
		assertThat(address.getDataTypeId()).isEqualTo(12L);
	}

	@Test
	void toResponse_mapsAllFields() {
		Address address = new Address();
		address.setId(1L);
		address.setRowId(42L);
		address.setDataTypeId(12L);
		address.setCityId(5L);
		address.setStreetName("Street");
		address.setHouseName("12");
		address.setAddrDesc("Desc");
		address.setPrimary(true);

		AddressResponse response = AddressMapper.toResponse(address);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.rowId()).isEqualTo(42L);
		assertThat(response.dataTypeId()).isEqualTo(12L);
		assertThat(response.cityId()).isEqualTo(5L);
		assertThat(response.streetName()).isEqualTo("Street");
		assertThat(response.houseName()).isEqualTo("12");
		assertThat(response.addrDesc()).isEqualTo("Desc");
		assertThat(response.primary()).isTrue();
	}

}

package com.etiya.crm.orderservice.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

	private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

	@Test
	void toSummaryResponse_mapsFieldsWithRenamedSourceColumns() {
		AddressResponse address = new AddressResponse(1L, 42L, 12L, 5L, "Street", "12", "Desc", true, null, null,
				null, null);

		AddressSummaryResponse summary = mapper.toSummaryResponse(address);

		assertThat(summary.addressId()).isEqualTo(1L);
		assertThat(summary.cityId()).isEqualTo(5L);
		assertThat(summary.streetName()).isEqualTo("Street");
		assertThat(summary.buildingName()).isEqualTo("12");
		assertThat(summary.addressDesc()).isEqualTo("Desc");
		assertThat(summary.cityName()).isNull();
	}

	@Test
	void toCreateAddressRequest_mapsRequestFieldsPlusExplicitParameters() {
		AddressInfoRequest request = new AddressInfoRequest(5L, "Street", "12", "Desc");

		CreateAddressRequest created = mapper.toCreateAddressRequest(request, 42L, 12L, true);

		assertThat(created.rowId()).isEqualTo(42L);
		assertThat(created.dataTypeId()).isEqualTo(12L);
		assertThat(created.cityId()).isEqualTo(5L);
		assertThat(created.houseName()).isEqualTo("12");
		assertThat(created.addrDesc()).isEqualTo("Desc");
		assertThat(created.primary()).isTrue();
	}

}

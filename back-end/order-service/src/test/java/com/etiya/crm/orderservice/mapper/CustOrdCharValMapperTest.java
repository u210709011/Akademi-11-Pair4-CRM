package com.etiya.crm.orderservice.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;

import static org.assertj.core.api.Assertions.assertThat;

class CustOrdCharValMapperTest {

	private final CustOrdCharValMapper mapper = Mappers.getMapper(CustOrdCharValMapper.class);

	@Test
	void toEntity_mapsCommonFields_andIgnoresManagedColumns() {
		ProdCharValRequest request = new ProdCharValRequest(1L, 2L, "Red");

		CustOrdCharVal entity = mapper.toEntity(request);

		assertThat(entity.getCharId()).isEqualTo(1L);
		assertThat(entity.getCharValId()).isEqualTo(2L);
		assertThat(entity.getVal()).isEqualTo("Red");
		assertThat(entity.getCustOrdCharValId()).isNull();
		assertThat(entity.getCustOrdItem()).isNull();
	}

	@Test
	void toResponse_mapsAllFields() {
		CustOrdCharVal entity = new CustOrdCharVal();
		entity.setCharId(1L);
		entity.setCharValId(2L);
		entity.setVal("Red");

		ProdCharValResponse response = mapper.toResponse(entity);

		assertThat(response.charId()).isEqualTo(1L);
		assertThat(response.charValId()).isEqualTo(2L);
		assertThat(response.val()).isEqualTo("Red");
	}

}

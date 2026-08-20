package com.etiya.crm.orderservice.mapper;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;

import static org.assertj.core.api.Assertions.assertThat;

class CustOrderItemMapperTest {

	private final CustOrderItemMapper mapper = Mappers.getMapper(CustOrderItemMapper.class);

	@Test
	void toSummaryResponse_mapsFieldsAndServiceStartDateFromCdate() {
		CustOrdItem item = new CustOrdItem();
		item.setCustOrdItemId(1L);
		item.setProdId(100L);
		item.setProdOfrId(200L);
		item.setProdSpecId(300L);
		item.setOfrName("Offer");
		item.setProdName("Product");
		item.setCmpgId(5L);
		item.setCmpgName("Campaign");
		Instant cdate = Instant.parse("2026-01-01T00:00:00Z");
		item.setCdate(cdate);

		ProdCharValResponse charVal = new ProdCharValResponse(1L, 2L, "Red");
		OrderItemSummaryResponse summary = mapper.toSummaryResponse(item, List.of(charVal));

		assertThat(summary.custOrdItemId()).isEqualTo(1L);
		assertThat(summary.prodId()).isEqualTo(100L);
		assertThat(summary.prodOfrId()).isEqualTo(200L);
		assertThat(summary.ofrName()).isEqualTo("Offer");
		assertThat(summary.serviceStartDate()).isEqualTo(cdate);
		assertThat(summary.charVals()).containsExactly(charVal);
	}

	@Test
	void toItemResponse_mapsCustOrdIdAndFormattedProdAndCmpgNumbers() {
		CustOrd custOrd = new CustOrd();
		custOrd.setCustOrdId(10L);
		CustOrdItem item = new CustOrdItem();
		item.setCustOrdItemId(1L);
		item.setCustOrd(custOrd);
		item.setProdId(100L);
		item.setProdName("Product");
		item.setCmpgId(5L);
		item.setCmpgName("Campaign");
		item.setCustAcctId(200L);

		CustOrdItemResponse response = mapper.toItemResponse(item);

		assertThat(response.custOrdId()).isEqualTo(10L);
		assertThat(response.prodNo()).isEqualTo("000100");
		assertThat(response.cmpgNo()).isEqualTo("000005");
		assertThat(response.custAcctId()).isEqualTo(200L);
	}

	@Test
	void toItemResponse_leavesProdAndCmpgNoNull_whenIdsAreNull() {
		CustOrd custOrd = new CustOrd();
		custOrd.setCustOrdId(10L);
		CustOrdItem item = new CustOrdItem();
		item.setCustOrd(custOrd);

		CustOrdItemResponse response = mapper.toItemResponse(item);

		assertThat(response.prodNo()).isNull();
		assertThat(response.cmpgNo()).isNull();
	}

	@Test
	void toEntity_mapsRequestFields_andIgnoresManagedColumns() {
		BasketItemRequest request = new BasketItemRequest(200L, 5L, List.of());

		CustOrdItem item = mapper.toEntity(request);

		assertThat(item.getProdOfrId()).isEqualTo(200L);
		assertThat(item.getCmpgId()).isEqualTo(5L);
		assertThat(item.getCustOrdItemId()).isNull();
		assertThat(item.getCustOrd()).isNull();
		assertThat(item.getProdId()).isNull();
	}

}

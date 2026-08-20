package com.etiya.crm.customerservice.business.concretes;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.clients.controllers.OrderClient;
import com.etiya.crm.shared.contracts.order.CustOrdItemResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceBillingAccountProductGuardTest {

	@Mock
	private OrderClient orderClient;

	@InjectMocks
	private OrderServiceBillingAccountProductGuard guard;

	@Test
	void hasLinkedProducts_true_whenOrderServiceReturnsItems() {
		when(orderClient.getItemsByCustAcctId(5L))
				.thenReturn(List.of(new CustOrdItemResponse(1L, 100L, "Fiber 100", null, null, 5L)));

		assertThat(guard.hasLinkedProducts(5L)).isTrue();
	}

	@Test
	void hasLinkedProducts_false_whenOrderServiceReturnsEmpty() {
		when(orderClient.getItemsByCustAcctId(5L)).thenReturn(List.of());

		assertThat(guard.hasLinkedProducts(5L)).isFalse();
	}
}

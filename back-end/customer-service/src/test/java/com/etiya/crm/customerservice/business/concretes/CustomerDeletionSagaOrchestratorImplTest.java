package com.etiya.crm.customerservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerDeletionSagaRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerDeletionSaga;
import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;
import com.etiya.crm.customerservice.entities.concretes.SagaStatus;
import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepNames;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDeletionSagaOrchestratorImplTest {

	@Mock
	private CustomerDeletionSagaRepository sagaRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerAccountRepository customerAccountRepository;

	@Mock
	private CustomerSearchViewRepository customerSearchViewRepository;

	@Mock
	private CustomerLookupResolver lookupResolver;

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@Mock
	private DltAlertNotifier dltAlertNotifier;

	@InjectMocks
	private CustomerDeletionSagaOrchestratorImpl orchestrator;

	@Test
	void start_savesSagaWithStartedStatus() {
		orchestrator.start(10L);

		ArgumentCaptor<CustomerDeletionSaga> captor = ArgumentCaptor.forClass(CustomerDeletionSaga.class);
		verify(sagaRepository).save(captor.capture());
		assertThat(captor.getValue().getCustId()).isEqualTo(10L);
		assertThat(captor.getValue().getStatus()).isEqualTo(SagaStatus.STARTED);
	}

	@Test
	void handleStepResult_ignoresResult_whenSagaNotFound() {
		when(sagaRepository.findById(10L)).thenReturn(Optional.empty());

		orchestrator.handleStepResult(10L, SagaStepNames.PARTY_DEACTIVATION, true, null);

		verify(customerRepository, never()).findById(any());
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void handleStepResult_staysStarted_whenOnlyOneStepSucceeded() {
		CustomerDeletionSaga saga = startedSaga(10L);
		when(sagaRepository.findById(10L)).thenReturn(Optional.of(saga));

		orchestrator.handleStepResult(10L, SagaStepNames.PARTY_DEACTIVATION, true, null);

		assertThat(saga.getStatus()).isEqualTo(SagaStatus.STARTED);
		assertThat(saga.getPartyDeactivationSuccess()).isTrue();
		assertThat(saga.getContactInfoSuccess()).isNull();
		verify(customerRepository, never()).findById(any());
	}

	@Test
	void handleStepResult_completesSaga_whenBothStepsSucceeded() {
		CustomerDeletionSaga saga = startedSaga(10L);
		saga.setPartyDeactivationSuccess(true);
		when(sagaRepository.findById(10L)).thenReturn(Optional.of(saga));

		orchestrator.handleStepResult(10L, SagaStepNames.CONTACT_INFO_DEACTIVATION, true, null);

		assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPLETED);
		verify(customerRepository, never()).findById(any());
	}

	@Test
	void handleStepResult_compensates_whenAStepFailed() {
		CustomerDeletionSaga saga = startedSaga(10L);
		when(sagaRepository.findById(10L)).thenReturn(Optional.of(saga));

		Customer customer = new Customer();
		customer.setCustId(10L);
		customer.setPartyRoleId(100L);
		customer.setActive(false);
		when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
		when(lookupResolver.resolveActiveAccountStatusId()).thenReturn(601L);
		when(lookupResolver.resolveCustomerDataTypeId()).thenReturn(1L);
		CustomerSearchView view = new CustomerSearchView();
		view.setDeleted(true);
		when(customerSearchViewRepository.findById(10L)).thenReturn(Optional.of(view));

		orchestrator.handleStepResult(10L, SagaStepNames.CONTACT_INFO_DEACTIVATION, false, "boom");

		assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
		assertThat(saga.getContactInfoSuccess()).isFalse();
		assertThat(customer.isActive()).isTrue();
		verify(customerRepository).save(customer);
		verify(customerAccountRepository).restoreByCustId(10L, 601L);
		assertThat(view.isDeleted()).isFalse();
		verify(customerSearchViewRepository).save(view);

		ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
		verify(outboxEventPublisher).publish(eq("customer"), eq("10"),
				eq(CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE), payloadCaptor.capture());
		CustomerDeletedEvent published = (CustomerDeletedEvent) payloadCaptor.getValue();
		assertThat(published.type()).isEqualTo(CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE);
		assertThat(published.custId()).isEqualTo(10L);
		assertThat(published.partyRoleId()).isEqualTo(100L);
		assertThat(published.dataTypeId()).isEqualTo(1L);

		verify(dltAlertNotifier).alert(eq("customer-service"), eq("CustomerDeletionSagaOrchestrator"),
				eq(SagaStepNames.CONTACT_INFO_DEACTIVATION), any(), eq("boom"));
	}

	@Test
	void handleStepResult_doesNotRetriggerCompensation_whenSagaAlreadyCompensated() {
		CustomerDeletionSaga saga = startedSaga(10L);
		saga.setStatus(SagaStatus.COMPENSATED);
		when(sagaRepository.findById(10L)).thenReturn(Optional.of(saga));

		orchestrator.handleStepResult(10L, SagaStepNames.PARTY_DEACTIVATION, true, null);

		assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
		verify(customerRepository, never()).findById(any());
		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	private CustomerDeletionSaga startedSaga(Long custId) {
		CustomerDeletionSaga saga = new CustomerDeletionSaga();
		saga.setCustId(custId);
		saga.setStatus(SagaStatus.STARTED);
		return saga;
	}
}

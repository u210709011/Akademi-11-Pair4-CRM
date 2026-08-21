package com.etiya.crm.customerservice.business.concretes;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etiya.crm.customerservice.business.abstracts.CustomerDeletionSagaOrchestrator;
import com.etiya.crm.customerservice.business.abstracts.CustomerLookupResolver;
import com.etiya.crm.customerservice.constants.AlertTags;
import com.etiya.crm.customerservice.constants.KafkaConsumerGroups;
import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerAccountRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerDeletionSagaRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerRepository;
import com.etiya.crm.customerservice.dataAccess.abstracts.CustomerSearchViewRepository;
import com.etiya.crm.customerservice.entities.concretes.Customer;
import com.etiya.crm.customerservice.entities.concretes.CustomerDeletionSaga;
import com.etiya.crm.customerservice.entities.concretes.SagaStatus;
import com.etiya.crm.shared.contracts.messaging.DltAlertNotifier;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.customer.CustomerDeletedEvent;
import com.etiya.crm.shared.events.customer.CustomerEventTypes;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import com.etiya.crm.shared.events.saga.SagaStepNames;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDeletionSagaOrchestratorImpl implements CustomerDeletionSagaOrchestrator {

	private final CustomerDeletionSagaRepository sagaRepository;
	private final CustomerRepository customerRepository;
	private final CustomerAccountRepository customerAccountRepository;
	private final CustomerSearchViewRepository customerSearchViewRepository;
	private final CustomerLookupResolver lookupResolver;
	private final OutboxEventPublisher outboxEventPublisher;
	private final DltAlertNotifier dltAlertNotifier;

	@Override
	@Transactional
	public void start(Long custId) {
		sagaRepository.save(CustomerDeletionSaga.started(custId));
		log.info(LogMessages.SAGA_STARTED, custId);
	}

	@Override
	@Transactional
	public void handleStepResult(Long custId, String stepName, boolean success, String reason) {
		log.info(LogMessages.SAGA_STEP_RESULT_RECEIVED, custId, stepName, success, reason);

		CustomerDeletionSaga saga = sagaRepository.findById(custId).orElse(null);
		if (saga == null) {
			log.warn(LogMessages.SAGA_NOT_FOUND, custId, stepName);
			return;
		}

		if (saga.getStatus() == SagaStatus.COMPENSATING || saga.getStatus() == SagaStatus.COMPLETED
				|| saga.getStatus() == SagaStatus.COMPENSATED) {
			log.info(LogMessages.SAGA_ALREADY_RESOLVED, custId, saga.getStatus(), stepName);
			applyStepField(saga, stepName, success);
			sagaRepository.save(saga);
			return;
		}

		applyStepField(saga, stepName, success);

		if (!success) {
			compensate(saga, stepName, reason);
			return;
		}

		if (Boolean.TRUE.equals(saga.getPartyDeactivationSuccess())
				&& Boolean.TRUE.equals(saga.getContactInfoSuccess())) {
			saga.setStatus(SagaStatus.COMPLETED);
			sagaRepository.save(saga);
			log.info(LogMessages.SAGA_COMPLETED, custId);
			return;
		}

		sagaRepository.save(saga);
	}

	private void applyStepField(CustomerDeletionSaga saga, String stepName, boolean success) {
		if (SagaStepNames.PARTY_DEACTIVATION.equals(stepName)) {
			saga.setPartyDeactivationSuccess(success);
		} else if (SagaStepNames.CONTACT_INFO_DEACTIVATION.equals(stepName)) {
			saga.setContactInfoSuccess(success);
		}
	}

	private void compensate(CustomerDeletionSaga saga, String failedStep, String reason) {
		Long custId = saga.getCustId();
		saga.setStatus(SagaStatus.COMPENSATING);
		sagaRepository.save(saga);
		log.error(LogMessages.SAGA_COMPENSATING, failedStep, reason, custId);
		dltAlertNotifier.alert(KafkaConsumerGroups.CUSTOMER_SERVICE, AlertTags.SAGA_ORCHESTRATOR, failedStep,
				AlertTags.SAGA_STEP_FAILED, reason);

		Customer customer = customerRepository.findById(custId).orElse(null);
		if (customer != null) {
			customer.setActive(true);
			customerRepository.save(customer);
			customerAccountRepository.restoreByCustId(custId, lookupResolver.resolveActiveAccountStatusId());
			customerSearchViewRepository.findById(custId).ifPresent(view -> {
				view.setDeleted(false);
				customerSearchViewRepository.save(view);
			});

			outboxEventPublisher.publish(KafkaTopics.CUSTOMER_AGGREGATE_TYPE, custId.toString(),
					CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE,
					new CustomerDeletedEvent(UUID.randomUUID(), CustomerEventTypes.CUSTOMER_DELETION_COMPENSATE,
							custId, customer.getPartyRoleId(), lookupResolver.resolveCustomerDataTypeId()));
		}

		saga.setStatus(SagaStatus.COMPENSATED);
		sagaRepository.save(saga);
		log.error(LogMessages.SAGA_COMPENSATED, custId);
	}
}

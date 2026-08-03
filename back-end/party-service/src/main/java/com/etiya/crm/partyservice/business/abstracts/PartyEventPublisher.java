package com.etiya.crm.partyservice.business.abstracts;

import com.etiya.crm.partyservice.entities.concretes.Individual;
import com.etiya.crm.shared.contracts.individual.CreateIndividualCommand;

/**
 * IndividualManager'in entity orkestrasyonundan (Party/Individual/PartyRole)
 * bagimsiz olarak "party-events" topic'ine event insa edip yayinlama
 * sorumlulugunu tasir - IndividualManager event govdesinin nasil kuruldugunu
 * ya da nereye yayinlandigini bilmek zorunda kalmaz.
 */
public interface PartyEventPublisher {

	/** Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri WAL'den okuyup "party-events" topic'ine yayinlar. */
	void publishIndividualPartyCreated(Long partyRoleId, CreateIndividualCommand command, Long partyRoleTypeId);

	/**
	 * customer-service'in PartyEventListener'i bu event'i zaten dinliyor ve
	 * CUSTOMER_SEARCH_VIEW'i senkronluyor. Rol UpdateIndividualCommand ile
	 * degismez ama tuketici tarafinda alan hep dolu kalsin diye PartyRole'den
	 * tekrar okunup payload'a eklenir.
	 */
	void publishIndividualUpdated(Long partyRoleId, Individual individual);
}

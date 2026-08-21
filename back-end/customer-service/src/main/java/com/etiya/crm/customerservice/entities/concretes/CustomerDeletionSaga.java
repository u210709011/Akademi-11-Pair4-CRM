package com.etiya.crm.customerservice.entities.concretes;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Musteri silme cascade saga'sinin durum makinesi - custId primary key. */
@Getter
@Setter
@Entity
@Table(name = "customer_deletion_saga")
public class CustomerDeletionSaga {

	@Id
	@Column(name = "cust_id")
	private Long custId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private SagaStatus status;

	/** null: adim henuz sonuclanmadi. */
	@Column(name = "party_deactivation_success")
	private Boolean partyDeactivationSuccess;

	/** null: adim henuz sonuclanmadi. */
	@Column(name = "contact_info_success")
	private Boolean contactInfoSuccess;

	@Column(name = "cdate", updatable = false)
	private Instant cdate;

	@Column(name = "udate")
	private Instant udate;

	@PrePersist
	private void onCreate() {
		cdate = Instant.now();
	}

	@PreUpdate
	private void onUpdate() {
		udate = Instant.now();
	}

	public static CustomerDeletionSaga started(Long custId) {
		CustomerDeletionSaga saga = new CustomerDeletionSaga();
		saga.setCustId(custId);
		saga.setStatus(SagaStatus.STARTED);
		return saga;
	}
}

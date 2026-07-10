package com.etiya.crm.customerservice.entities.concretes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Arama, kisi bilgisi (party-service) ile musteri bilgisini (bu servis)
 * birlikte gerektirir. party-service event'leriyle guncel tutulan
 * denormalize edilmis read-model.
 */
@Getter
@Setter
@Entity
@Table(name = "customer_search_view",
		indexes = {
				@Index(name = "idx_csv_lastname", columnList = "lastName"),
				@Index(name = "idx_csv_tc", columnList = "tcNo")
		})
public class CustomerSearchView {

	@Id
	private Long custId;

	private Long partyRoleId;
	private String firstName;
	private String lastName;
	private String tcNo;
	private String acctNo;
	private String status;
	private boolean deleted;
}

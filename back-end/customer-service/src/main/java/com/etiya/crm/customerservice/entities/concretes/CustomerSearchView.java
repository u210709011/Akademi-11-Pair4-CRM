package com.etiya.crm.customerservice.entities.concretes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Event'lerle güncellenen, arama için denormalize edilmiş read-modeldir. */
@Getter
@Setter
@Entity
@Table(name = "customer_search_view",
		indexes = {
			@Index(name = "idx_csv_lastname", columnList = "lastName"),
			@Index(name = "idx_csv_firstname", columnList = "firstName"),
			@Index(name = "idx_csv_acctno", columnList = "acctNo"),
			@Index(name = "idx_csv_tc", columnList = "tcNo"),
			@Index(name = "idx_csv_gsm", columnList = "gsm")
		})
public class CustomerSearchView {

	@Id
	private Long custId;

	private Long partyRoleId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String tcNo;
	private String acctNo;
	private String status;

	// party-service
	private String role;

	private Long partyRoleTypeId;

	// contact-info-service
	private String gsm;



	private boolean deleted;
}

package com.etiya.crm.customerservice.entities.concretes;

import java.util.ArrayList;
import java.util.List;

import com.etiya.crm.customerservice.entities.abstracts.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cust")
/** Müşteri aggregate'inin servis içindeki kalıcı modelidir. */
public class Customer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_id")
	private Long custId;

	// party-service'e logical referans
	@Column(name = "party_role_id", nullable = false)
	private Long partyRoleId;

	@Column(name = "cust_tp_id")
	private Long custTpId;

	@OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<CustomerAccount> accounts = new ArrayList<>();
}

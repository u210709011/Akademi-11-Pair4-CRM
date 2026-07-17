package com.etiya.crm.customerservice.entities.concretes;

import com.etiya.crm.customerservice.entities.abstracts.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cust_acct")
public class CustomerAccount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_acct_id")
	private Long custAcctId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cust_id", nullable = false)
	private Customer customer;

	@Column(name = "acct_no", nullable = false)
	private String accountNo;

	@Column(name = "acct_name")
	private String accountName;

	@Column(name = "acct_desc", length = 500)
	private String accountDesc;

	@Column(name = "acct_tp_id")
	private Long accountTpId;

	// contact-info-service'teki Address'e logical referans (fatura hesabinin
	// adresi). FK DEGIL, sadece Long.
	@Column(name = "address_id")
	private Long addressId;
}

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

	@Column(name = "acct_tp_id")
	private Long accountTpId;

	// lookup-service uzerindeki lifecycle durumu (PENDING / ACTIVE / SUSPENDED / ...).
	// Soft-delete icin bu degil, BaseEntity.active kullanilir.
	@Column(name = "st_id", nullable = false)
	private Long stId;
}

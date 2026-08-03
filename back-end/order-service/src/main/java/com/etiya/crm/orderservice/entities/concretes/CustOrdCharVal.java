package com.etiya.crm.orderservice.entities.concretes;

import com.etiya.crm.orderservice.entities.abstracts.BaseEntity;

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
@Table(name = "cust_ord_char_val")
public class CustOrdCharVal extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_ord_char_val_id")
	private Long custOrdCharValId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cust_ord_id", nullable = false)
	private CustOrd custOrd;

	@Column(name = "char_id", nullable = false)
	private Long charId;

	@Column(name = "char_val_id", nullable = false)
	private Long charValId;

	@Column(name = "val")
	private String val;
}

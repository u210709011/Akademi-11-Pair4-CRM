package com.etiya.crm.orderservice.entities.concretes;

import java.util.ArrayList;
import java.util.List;

import com.etiya.crm.orderservice.entities.abstracts.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cust_ord")
public class CustOrd extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_ord_id")
	private Long custOrdId;

	@Column(name = "ord_st_id")
	private Long ordStId;

	@Column(name = "cust_id", nullable = false)
	private Long custId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_id")
	private BsnInter bsnInter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_spec_id")
	private BsnInterSpec bsnInterSpec;

	//soft delete 
	@OneToMany(mappedBy = "custOrd", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<CustOrdItem> items = new ArrayList<>();

	//soft delete 
	@OneToMany(mappedBy = "custOrd", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<CustOrdCharVal> charVals = new ArrayList<>();
}

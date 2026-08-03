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
@Table(name = "cust_ord_item")
public class CustOrdItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_ord_item_id")
	private Long custOrdItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cust_ord_id", nullable = false)
	private CustOrd custOrd;

	@Column(name = "cust_acct_id")
	private Long custAcctId;

	// Hesap devri senaryosu icin hedef cust_acct 'a referans, FR-008..021 kapsaminda kullanilmiyor, simdilik daima null kalir.
	@Column(name = "new_cust_acct_id")
	private Long newCustAcctId;

	@Column(name = "prod_id")
	private Long prodId;

	@Column(name = "prod_ofr_id")
	private Long prodOfrId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_id")
	private BsnInter bsnInter;

	@Column(name = "cmpg_id")
	private Long cmpgId;

	@Column(name = "is_need_shpmt")
	private Boolean isNeedShpmt;

	// Sipariş anındaki adı döndüren alan
	@Column(name = "ofr_name")
	private String ofrName;

	@Column(name = "prod_name")
	private String prodName;

	@Column(name = "prod_spec_id")
	private Long prodSpecId;

	@Column(name = "cust_id")
	private Long custId;

	// Müşteri devri senaryosu icin hedef musteriye referans, FR-008..021 kapsaminda kullanilmiyor, simdilik daima null kalir.
	@Column(name = "new_cust_id")
	private Long newCustId;

	@Column(name = "cmpg_name")
	private String cmpgName;
}

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
@Table(name = "bsn_inter")
public class BsnInter extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bsn_inter_id")
	private Long bsnInterId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_spec_id")
	private BsnInterSpec bsnInterSpec;
	
	@Column(name = "cust_id")
	private Long custId;

	@Column(name = "descr")
	private String descr;

	@Column(name = "bsn_inter_st_id")
	private Long bsnInterStId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prnt_bsn_inter_id")
	private BsnInter parent;

	//soft delete
	@OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<BsnInter> children = new ArrayList<>();

	@Column(name = "row_id")
	private Long rowId;

	@Column(name = "data_tp_id")
	private Long dataTpId;

	//soft delete
	@OneToMany(mappedBy = "bsnInter", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<BsnInterItem> items = new ArrayList<>();
}

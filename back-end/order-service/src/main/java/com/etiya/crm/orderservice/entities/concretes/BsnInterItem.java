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
@Table(name = "bsn_inter_item")
public class BsnInterItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bsn_inter_item_id")
	private Long bsnInterItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_id", nullable = false)
	private BsnInter bsnInter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsn_inter_spec_id")
	private BsnInterSpec bsnInterSpec;

	@Column(name = "descr")
	private String descr;

	@Column(name = "row_id")
	private Long rowId;

	@Column(name = "data_tp_id")
	private Long dataTpId;

	@Column(name = "actn_tp_id")
	private Long actnTpId;

	@Column(name = "st_id")
	private Long stId;
}

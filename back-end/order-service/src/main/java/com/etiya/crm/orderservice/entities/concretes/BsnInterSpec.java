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
@Table(name = "bsn_inter_spec")
public class BsnInterSpec extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bsn_inter_spec_id")
	private Long bsnInterSpecId;

	@Column(name = "bsn_inter_tp_id")
	private Long bsnInterTpId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prnt_bsn_inter_spec_id")
	private BsnInterSpec parent;

	//soft delete
	@OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<BsnInterSpec> children = new ArrayList<>();

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String descr;

	@Column(name = "shrt_code")
	private String shrtCode;
}

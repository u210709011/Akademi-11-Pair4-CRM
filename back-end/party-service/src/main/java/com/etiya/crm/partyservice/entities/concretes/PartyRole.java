package com.etiya.crm.partyservice.entities.concretes;

import com.etiya.crm.partyservice.entities.abstracts.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "party_role")
public class PartyRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_role_id")
    private Long partyRoleId;

    @Column(name = "party_role_tp_id",nullable = false)
    private Long partyRoleTypeId;

    @Column(name = "st_id",nullable = false)
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;
}

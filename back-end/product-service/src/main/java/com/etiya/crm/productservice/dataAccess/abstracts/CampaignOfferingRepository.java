package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignOfferingRepository extends JpaRepository<CampaignOffering,Long> {
    List<CampaignOffering> findByCampaign_CampaignId(Long campaignId);
}

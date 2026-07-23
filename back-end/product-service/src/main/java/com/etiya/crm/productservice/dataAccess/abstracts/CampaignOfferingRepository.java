package com.etiya.crm.productservice.dataAccess.abstracts;

import com.etiya.crm.productservice.entities.concretes.CampaignOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignOfferingRepository extends JpaRepository<CampaignOffering,Long> {
}

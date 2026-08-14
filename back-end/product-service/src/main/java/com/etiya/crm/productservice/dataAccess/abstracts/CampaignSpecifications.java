package com.etiya.crm.productservice.dataAccess.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.etiya.crm.productservice.entities.concretes.Campaign;

import jakarta.persistence.criteria.Predicate;

/**
 * FR-013 ACC-008: /product-campaigns artik tum kayitlari degil, verilen filtrelerle
 * kisitlanmis (ve sayfalanmis) bir sonuc doner - bkz. ProductOfferingSpecifications
 * (ayni desen, ayni AND gerekcesi).
 */
public final class CampaignSpecifications {

	private CampaignSpecifications() {
	}

	public static Specification<Campaign> search(Long campaignId, String name) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (campaignId != null) {
				predicates.add(cb.equal(root.get("campaignId"), campaignId));
			}
			if (StringUtils.hasText(name)) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
			}
			return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}

package com.etiya.crm.productservice.dataAccess.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.etiya.crm.productservice.entities.concretes.ProductOffering;

import jakarta.persistence.criteria.Predicate;

/**
 * FR-013 ACC-004: /product-offerings artik tum kayitlari degil, verilen filtrelerle
 * kisitlanmis (ve sayfalanmis) bir sonuc doner. id ve name ayri filtre boyutlari
 * oldugu icin (customer-service'teki firstName/lastName gibi tek bir kimlik grubunun
 * alternatifleri degil) ikisi de verilirse AND'lenir.
 */
public final class ProductOfferingSpecifications {

	private ProductOfferingSpecifications() {
	}

	public static Specification<ProductOffering> search(Long productOfferingId, String name) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (productOfferingId != null) {
				predicates.add(cb.equal(root.get("productOfferingId"), productOfferingId));
			}
			if (StringUtils.hasText(name)) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
			}
			return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}

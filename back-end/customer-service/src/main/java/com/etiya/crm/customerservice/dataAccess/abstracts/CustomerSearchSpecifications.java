package com.etiya.crm.customerservice.dataAccess.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

import jakarta.persistence.criteria.Predicate;

/**
 * Arama kurali: firstName ve lastName ikisi de verilmisse birlikte (AND)
 * degerlendirilir; sadece biri verilmisse tek basina kullanilir. tcNo/acctNo
 * gibi diger alanlar bu isim grubuyla ve birbirleriyle her zaman OR'lanir.
 * Silinmis (deleted) kayitlar sonuca hicbir zaman dahil edilmez.
 */
public final class CustomerSearchSpecifications {

	private CustomerSearchSpecifications() {
	}

	public static Specification<CustomerSearchView> search(String firstName, String lastName, String tcNo,
			String acctNo) {
		return (root, query, cb) -> {
			List<Predicate> orGroup = new ArrayList<>();

			boolean hasFirstName = StringUtils.hasText(firstName);
			boolean hasLastName = StringUtils.hasText(lastName);

			if (hasFirstName && hasLastName) {
				orGroup.add(cb.and(
						cb.like(cb.lower(root.get("firstName")), like(firstName)),
						cb.like(cb.lower(root.get("lastName")), like(lastName))));
			} else if (hasFirstName) {
				orGroup.add(cb.like(cb.lower(root.get("firstName")), like(firstName)));
			} else if (hasLastName) {
				orGroup.add(cb.like(cb.lower(root.get("lastName")), like(lastName)));
			}

			if (StringUtils.hasText(tcNo)) {
				orGroup.add(cb.equal(root.get("tcNo"), tcNo));
			}
			if (StringUtils.hasText(acctNo)) {
				orGroup.add(cb.equal(root.get("acctNo"), acctNo));
			}

			Predicate notDeleted = cb.isFalse(root.get("deleted"));
			if (orGroup.isEmpty()) {
				return notDeleted;
			}
			return cb.and(notDeleted, cb.or(orGroup.toArray(new Predicate[0])));
		};
	}

	private static String like(String value) {
		return "%" + value.toLowerCase() + "%";
	}
}

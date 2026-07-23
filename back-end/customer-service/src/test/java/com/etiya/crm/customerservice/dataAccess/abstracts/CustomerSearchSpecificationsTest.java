package com.etiya.crm.customerservice.dataAccess.abstracts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.etiya.crm.customerservice.entities.concretes.CustomerSearchView;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ONEMLI is kurali: firstName/lastName AND ile tek bir grup olustururken,
 * tcNo/acctNo/custId/gsm bu grupla ve birbirleriyle HER ZAMAN OR'lanir. Bu
 * testler tam olarak bu OR/AND yapisini (cb.and/cb.or cagri sirasi ve
 * argumanlari) dogrular - implementasyon detayi gibi gorunse de, bu sinifin
 * TEK sorumlulugu bu kombinasyon mantigi oldugu icin degerli bir regression testi.
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class CustomerSearchSpecificationsTest {

	@Mock
	private Root<CustomerSearchView> root;

	@Mock
	private CriteriaQuery<?> query;

	@Mock
	private CriteriaBuilder cb;

	private <Y> Path<Y> pathFor(String attribute) {
		Path<Y> path = mock(Path.class);
		when(root.<Y>get(attribute)).thenReturn(path);
		return path;
	}

	@Test
	void search_returnsOnlyNotDeletedPredicate_whenNoFiltersGiven() {
		Path<Boolean> deletedPath = pathFor("deleted");
		Predicate notDeleted = mock(Predicate.class, "notDeleted");
		when(cb.isFalse(deletedPath)).thenReturn(notDeleted);

		Specification<CustomerSearchView> spec = CustomerSearchSpecifications.search(null, null, null, null, null,
				null);
		Predicate result = spec.toPredicate(root, query, cb);

		assertThat(result).isSameAs(notDeleted);
		verify(cb, never()).or(new Predicate[0]);
	}

	@Test
	void search_treatsBlankStrings_asNotProvided() {
		Path<Boolean> deletedPath = pathFor("deleted");
		Predicate notDeleted = mock(Predicate.class, "notDeleted");
		when(cb.isFalse(deletedPath)).thenReturn(notDeleted);

		Specification<CustomerSearchView> spec = CustomerSearchSpecifications.search("   ", "", null, "", null, "  ");
		Predicate result = spec.toPredicate(root, query, cb);

		assertThat(result).isSameAs(notDeleted);
	}

	@Test
	void search_usesFirstNameAlone_whenOnlyFirstNameGiven() {
		Path<String> firstNamePath = pathFor("firstName");
		Path<Boolean> deletedPath = pathFor("deleted");
		Expression<String> lowerFirstName = mock(Expression.class, "lowerFirstName");
		Predicate likeFirstName = mock(Predicate.class, "likeFirstName");
		Predicate notDeleted = mock(Predicate.class, "notDeleted");
		Predicate orResult = mock(Predicate.class, "orResult");
		Predicate finalAnd = mock(Predicate.class, "finalAnd");

		when(cb.lower(firstNamePath)).thenReturn(lowerFirstName);
		when(cb.like(lowerFirstName, "%ahmet%")).thenReturn(likeFirstName);
		when(cb.isFalse(deletedPath)).thenReturn(notDeleted);
		when(cb.or(likeFirstName)).thenReturn(orResult);
		when(cb.and(notDeleted, orResult)).thenReturn(finalAnd);

		Specification<CustomerSearchView> spec = CustomerSearchSpecifications.search("Ahmet", null, null, null, null,
				null);
		Predicate result = spec.toPredicate(root, query, cb);

		assertThat(result).isSameAs(finalAnd);
		verify(cb, never()).and(likeFirstName, likeFirstName);
	}

	@Test
	void search_combinesFirstAndLastNameWithAnd_thenOrsWithEveryOtherFilter() {
		Path<String> firstNamePath = pathFor("firstName");
		Path<String> lastNamePath = pathFor("lastName");
		Path<String> tcNoPath = pathFor("tcNo");
		Path<String> acctNoPath = pathFor("acctNo");
		Path<Long> custIdPath = pathFor("custId");
		Path<String> gsmPath = pathFor("gsm");
		Path<Boolean> deletedPath = pathFor("deleted");

		Expression<String> lowerFirstName = mock(Expression.class, "lowerFirstName");
		Expression<String> lowerLastName = mock(Expression.class, "lowerLastName");
		Predicate likeFirstName = mock(Predicate.class, "likeFirstName");
		Predicate likeLastName = mock(Predicate.class, "likeLastName");
		Predicate nameAnd = mock(Predicate.class, "nameAnd");
		Predicate tcNoEq = mock(Predicate.class, "tcNoEq");
		Predicate acctNoEq = mock(Predicate.class, "acctNoEq");
		Predicate custIdEq = mock(Predicate.class, "custIdEq");
		Predicate gsmEq = mock(Predicate.class, "gsmEq");
		Predicate notDeleted = mock(Predicate.class, "notDeleted");
		Predicate orResult = mock(Predicate.class, "orResult");
		Predicate finalAnd = mock(Predicate.class, "finalAnd");

		when(cb.lower(firstNamePath)).thenReturn(lowerFirstName);
		when(cb.lower(lastNamePath)).thenReturn(lowerLastName);
		when(cb.like(lowerFirstName, "%ahmet%")).thenReturn(likeFirstName);
		when(cb.like(lowerLastName, "%yilmaz%")).thenReturn(likeLastName);
		when(cb.and(likeFirstName, likeLastName)).thenReturn(nameAnd);
		when(cb.equal(tcNoPath, "10000000146")).thenReturn(tcNoEq);
		when(cb.equal(acctNoPath, "ACC-1")).thenReturn(acctNoEq);
		when(cb.equal(custIdPath, 1L)).thenReturn(custIdEq);
		when(cb.equal(gsmPath, "5551234567")).thenReturn(gsmEq);
		when(cb.isFalse(deletedPath)).thenReturn(notDeleted);
		// Sira onemli: name-grubu, tcNo, acctNo, custId, gsm (bkz. kaynak koddaki ekleme sirasi).
		when(cb.or(nameAnd, tcNoEq, acctNoEq, custIdEq, gsmEq)).thenReturn(orResult);
		when(cb.and(notDeleted, orResult)).thenReturn(finalAnd);

		Specification<CustomerSearchView> spec = CustomerSearchSpecifications.search("Ahmet", "Yilmaz",
				"10000000146", "ACC-1", 1L, "5551234567");
		Predicate result = spec.toPredicate(root, query, cb);

		assertThat(result).isSameAs(finalAnd);
	}

	@Test
	void search_ORsTcNoWithNameGroup_evenWhenNeitherMatchesTheOther() {
		// Kullanicinin ozellikle vurguladigi kural: "mert sahin 10003330055" ->
		// hem o tcNo'ya sahip kisi hem "mert sahin" adiyla eslesen(ler) doner (OR).
		Path<String> firstNamePath = pathFor("firstName");
		Path<String> lastNamePath = pathFor("lastName");
		Path<String> tcNoPath = pathFor("tcNo");
		Path<Boolean> deletedPath = pathFor("deleted");

		Expression<String> lowerFirstName = mock(Expression.class, "lowerFirstName");
		Expression<String> lowerLastName = mock(Expression.class, "lowerLastName");
		Predicate likeFirstName = mock(Predicate.class, "likeFirstName");
		Predicate likeLastName = mock(Predicate.class, "likeLastName");
		Predicate nameAnd = mock(Predicate.class, "nameAnd");
		Predicate tcNoEq = mock(Predicate.class, "tcNoEq");
		Predicate notDeleted = mock(Predicate.class, "notDeleted");
		Predicate orResult = mock(Predicate.class, "orResult");
		Predicate finalAnd = mock(Predicate.class, "finalAnd");

		when(cb.lower(firstNamePath)).thenReturn(lowerFirstName);
		when(cb.lower(lastNamePath)).thenReturn(lowerLastName);
		when(cb.like(lowerFirstName, "%mert%")).thenReturn(likeFirstName);
		when(cb.like(lowerLastName, "%sahin%")).thenReturn(likeLastName);
		when(cb.and(likeFirstName, likeLastName)).thenReturn(nameAnd);
		when(cb.equal(tcNoPath, "10003330055")).thenReturn(tcNoEq);
		when(cb.isFalse(deletedPath)).thenReturn(notDeleted);
		// CriteriaBuilder.or(...) hem 2-arg'li Expression<Boolean> overload'una HEM DE
		// varargs Predicate... overload'una sahiptir. Kaynak kod cb.or(array) cagirir
		// (orGroup.toArray(...)) - sadece VARARGS ile eslesir. cb.or(nameAnd, tcNoEq) iki
		// dogrudan argumanla yazilirsa Java 2-arg overload'u secer (yanlis method!) - bu
		// yuzden burada acikca bir Predicate[] gecirilir (5/1 elemanli diger testlerde
		// baska uygun overload olmadigindan varargs zaten dogru secilir, bu sorun sadece
		// TAM OLARAK 2 elemanli or/and cagrilarinda ortaya cikar).
		when(cb.or(new Predicate[] { nameAnd, tcNoEq })).thenReturn(orResult);
		when(cb.and(notDeleted, orResult)).thenReturn(finalAnd);

		Specification<CustomerSearchView> spec = CustomerSearchSpecifications.search("mert", "sahin", "10003330055",
				null, null, null);
		Predicate result = spec.toPredicate(root, query, cb);

		assertThat(result).isSameAs(finalAnd);
		// name grubu ile tcNo BIRBIRINE AND'lenmedi (bagimsiz OR dallari).
		verify(cb, never()).and(nameAnd, tcNoEq);
	}
}

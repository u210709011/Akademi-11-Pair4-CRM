package com.etiya.crm.shared.contracts.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Client'tan HAM bir lookup-service id'si (cityId, genderId gibi) geldiginde, o id'nin
 * gercekten var/aktif/dogru grupta olup olmadigini dogrular (bkz. B-07/B-14: cityId hicbir
 * yerde dogrulanmiyordu, uydurma bir id'yle bile adres olusturulabiliyordu; B-21:
 * order-service'in kendi adres govdesi ayni kontrolden gecmiyordu - bu yuzden
 * customer-service'ten buraya, shared-contracts'e tasindi, boylece her iki servis de -
 * ve lookup-service body'si alan ileriki bir servis de - ayni kontrolu paylasir).
 *
 * SADECE client-kaynakli ham id'ler icin kullanilir - backend'in kendi resolveTypeId(group, code)
 * ile urettigi id'ler (accountTpId, acctStId gibi) buna ihtiyac duymaz, zaten yapisal olarak
 * gecerlidir.
 *
 * Kullanan her servis kendi {@link LookupExistenceChecker} implementasyonunu (genelde
 * LookupCacheServiceImpl) Spring bean'i olarak saglamalidir - {@link ExistsInLookupGroupValidator}
 * bunu constructor injection ile alir.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsInLookupGroupValidator.class)
public @interface ExistsInLookupGroup {

	/** lookup-service GNL_TP tablosundaki ent_code_name (grup) degeri, orn. GnlTpGroups.CITY. */
	String group();

	/** Cagiran servis normalde kendi mesaj key'iyle ({@code message = "{...}"}) override eder. */
	String message() default "Invalid or nonexistent lookup id.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

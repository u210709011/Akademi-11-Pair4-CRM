package com.etiya.crm.customerservice.business.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Client'tan HAM bir lookup-service id'si (cityId, genderId gibi) geldiginde, o id'nin
 * gercekten var/aktif/dogru grupta olup olmadigini dogrular (bkz. B-07/B-14: cityId hicbir
 * yerde dogrulanmiyordu, uydurma bir id'yle bile adres olusturulabiliyordu).
 *
 * SADECE client-kaynakli ham id'ler icin kullanilir - backend'in kendi resolveTypeId(group, code)
 * ile urettigi id'ler (accountTpId, acctStId gibi) buna ihtiyac duymaz, zaten yapisal olarak
 * gecerlidir (bkz. CustomerLookupResolver).
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsInLookupGroupValidator.class)
public @interface ExistsInLookupGroup {

	/** lookup-service GNL_TP tablosundaki ent_code_name (grup) degeri, orn. GnlTpGroups.CITY. */
	String group();

	String message() default "{" + MessageKeys.INVALID_REQUEST_PARAMETER + "}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

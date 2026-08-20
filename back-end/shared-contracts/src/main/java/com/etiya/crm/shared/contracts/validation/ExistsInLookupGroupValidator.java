package com.etiya.crm.shared.contracts.validation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Spring'in SpringConstraintValidatorFactory'si sayesinde (Spring Boot validation
 * auto-configuration ile otomatik aktif) bu sinif normal bir Spring bean'i gibi
 * constructor injection alabilir - {@link LookupExistenceChecker}'i her servis kendi Spring
 * context'inde saglar (tipik olarak servisin kendi LookupCacheServiceImpl'i), o yuzden burada
 * hangi servisin calistigina dair bir kod olmadan tek bir validator tum servislerde calisir.
 */
@Component
public class ExistsInLookupGroupValidator implements ConstraintValidator<ExistsInLookupGroup, Long> {

	private final LookupExistenceChecker lookupExistenceChecker;

	private String group;

	public ExistsInLookupGroupValidator(LookupExistenceChecker lookupExistenceChecker) {
		this.lookupExistenceChecker = lookupExistenceChecker;
	}

	@Override
	public void initialize(ExistsInLookupGroup annotation) {
		this.group = annotation.group();
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		// null: @NotNull/@NotBlank'in isi, burada null her zaman gecerli sayilir (Bean Validation
		// konvansiyonu - bkz. @Pattern/@Size'in kendi built-in davranisi).
		return value == null || lookupExistenceChecker.existsInGroup(value, group);
	}
}

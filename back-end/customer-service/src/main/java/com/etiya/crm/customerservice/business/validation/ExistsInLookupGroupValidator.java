package com.etiya.crm.customerservice.business.validation;

import org.springframework.stereotype.Component;

import com.etiya.crm.customerservice.business.abstracts.LookupCacheService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * Spring'in SpringConstraintValidatorFactory'si sayesinde (Spring Boot validation
 * auto-configuration ile otomatik aktif) bu sinif normal bir Spring bean'i gibi
 * constructor injection alabilir.
 */
@Component
@RequiredArgsConstructor
public class ExistsInLookupGroupValidator implements ConstraintValidator<ExistsInLookupGroup, Long> {

	private final LookupCacheService lookupCacheService;

	private String group;

	@Override
	public void initialize(ExistsInLookupGroup annotation) {
		this.group = annotation.group();
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		// null: @NotNull/@NotBlank'in isi, burada null her zaman gecerli sayilir (Bean Validation
		// konvansiyonu - bkz. @Pattern/@Size'in kendi built-in davranisi).
		return value == null || lookupCacheService.existsInGroup(value, group);
	}
}

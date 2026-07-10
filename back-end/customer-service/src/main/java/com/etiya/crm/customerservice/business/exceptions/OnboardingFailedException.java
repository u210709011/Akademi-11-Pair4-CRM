package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** party/customer/contact-address servislerinden biri onboarding sirasinda basarisiz olursa. */
public class OnboardingFailedException extends BusinessException {

	public OnboardingFailedException(Throwable cause) {
		super(MessageKeys.ONBOARDING_FAILED, cause);
	}
}

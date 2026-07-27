package com.etiya.crm.apigateway.auth.exceptions;

/** Mesaj metnini kendisi tasimaz; messages/messages.properties'teki bir key tasir. */
public class AccountLockedException extends RuntimeException {

	public AccountLockedException(String messageKey) {
		super(messageKey);
	}
}

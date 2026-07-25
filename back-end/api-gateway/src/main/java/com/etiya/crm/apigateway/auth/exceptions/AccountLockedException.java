package com.etiya.crm.apigateway.auth.exceptions;

public class AccountLockedException extends RuntimeException {

	public AccountLockedException(String message) {
		super(message);
	}
}

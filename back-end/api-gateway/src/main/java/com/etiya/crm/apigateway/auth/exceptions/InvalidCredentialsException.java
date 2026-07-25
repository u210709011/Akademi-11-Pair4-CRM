package com.etiya.crm.apigateway.auth.exceptions;

/** Mesaj metnini kendisi tasimaz; messages/messages.properties'teki bir key tasir. */
public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException(String messageKey) {
		super(messageKey);
	}
}

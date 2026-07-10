package com.etiya.crm.customerservice.business.exceptions;

/**
 * Butun business exception'larin ortak atasi. Mesaj metnini kendisi
 * tasimaz; messages/messages.properties'teki bir key + argumanlari tasir.
 * Cozumleme (localization) GlobalExceptionHandler'da MessageSource ile yapilir.
 */
public abstract class BusinessException extends RuntimeException {

	private final String messageKey;
	private final transient Object[] args;

	protected BusinessException(String messageKey, Object... args) {
		super(messageKey);
		this.messageKey = messageKey;
		this.args = args;
	}

	protected BusinessException(String messageKey, Throwable cause) {
		super(messageKey, cause);
		this.messageKey = messageKey;
		this.args = new Object[0];
	}

	public String getMessageKey() {
		return messageKey;
	}

	public Object[] getArgs() {
		return args;
	}
}

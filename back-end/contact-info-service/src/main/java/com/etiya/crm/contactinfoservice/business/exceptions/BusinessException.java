package com.etiya.crm.contactinfoservice.business.exceptions;

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

	public String getMessageKey() {
		return messageKey;
	}

	public Object[] getArgs() {
		return args;
	}
}

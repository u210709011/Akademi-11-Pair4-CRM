package com.etiya.crm.productservice.business.exceptions;

public abstract class BusinessException extends RuntimeException {
    private final String messageKey;
    private final transient Object[] args;

    protected BusinessException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
    public String getMessageKey() { return messageKey; }
    public Object[] getArgs() { return args; }
}
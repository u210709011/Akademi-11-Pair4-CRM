package com.etiya.crm.apigateway.auth.exceptions;

import com.etiya.crm.apigateway.auth.constants.MessageKeys;
import com.etiya.crm.shared.contracts.error.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice(basePackages = "com.etiya.crm.apigateway.auth")
public class AuthExceptionHandler {

	private final MessageSource messageSource;

	public AuthExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
			ServerWebExchange exchange) {
		return build(HttpStatus.UNAUTHORIZED, resolve(ex.getMessage()), exchange);
	}

	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException ex, ServerWebExchange exchange) {
		return build(HttpStatus.LOCKED, resolve(ex.getMessage()), exchange);
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ErrorResponse> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
		String message = ex.getFieldErrors().stream()
				.findFirst()
				.map(FieldError::getDefaultMessage)
				.orElseGet(() -> resolve(MessageKeys.VALIDATION_FAILED));
		return build(HttpStatus.BAD_REQUEST, message, exchange);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, ServerWebExchange exchange) {
		String path = exchange.getRequest().getPath().value();
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, path));
	}

	private String resolve(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}

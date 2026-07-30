package com.etiya.crm.partyservice.business.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.partyservice.constants.MessageKeys;
import com.etiya.crm.shared.contracts.error.AbstractDownstreamExceptionHandler;
import com.etiya.crm.shared.contracts.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractDownstreamExceptionHandler {

	private final MessageSource messageSource;

	public GlobalExceptionHandler(MessageSource messageSource, ObjectMapper objectMapper) {
		super(objectMapper);
		this.messageSource = messageSource;
	}

	@Override
	protected String downstreamCallFailedMessage() {
		return resolve(MessageKeys.DOWNSTREAM_CALL_FAILED);
	}

	@Override
	protected String downstreamUnavailableMessage() {
		return resolve(MessageKeys.DOWNSTREAM_UNAVAILABLE);
	}

	@ExceptionHandler(DuplicateNationalIdException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateNationalId(DuplicateNationalIdException ex,
			HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler({ PartyRoleNotFoundException.class, IndividualNotFoundException.class,
			PartyNotFoundException.class, LookupValueNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleNotFound(BusinessException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), resolve(MessageKeys.VALIDATION_FAILED),
				request.getRequestURI(), errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, resolve(MessageKeys.UNEXPECTED_ERROR), request);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, BusinessException ex, HttpServletRequest request) {
		return build(status, resolve(ex.getMessageKey(), ex.getArgs()), request);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		ErrorResponse errorResponse = ErrorResponse.of(status.value(), status.getReasonPhrase(), message,
				request.getRequestURI());
		return ResponseEntity.status(status).body(errorResponse);
	}

	private String resolve(String key, Object... args) {
		return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
	}
}

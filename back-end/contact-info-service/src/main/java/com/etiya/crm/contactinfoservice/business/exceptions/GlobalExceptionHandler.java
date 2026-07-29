package com.etiya.crm.contactinfoservice.business.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.contactinfoservice.constants.MessageKeys;
import com.etiya.crm.shared.contracts.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private final MessageSource messageSource;

	@ExceptionHandler({ AddressNotFoundException.class, ContactMediumNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleNotFound(BusinessException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(InvalidContactMediumFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormat(InvalidContactMediumFormatException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex, request);
	}

	// customer-service'teki ayni kurallarin (AddressLimitExceededException/PrimaryAddressCannotBeDeletedException)
	// durum koduyla tutarli olsun diye 409 (Conflict) doner - ikisi de "mevcut durumla celisen bir istek" anlamina gelir.
	@ExceptionHandler({ AddressLimitExceededException.class, PrimaryAddressDeletionException.class,
			AddressLinkedToAccountException.class })
	public ResponseEntity<ErrorResponse> handleGuardViolation(BusinessException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> validationErrors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), resolve(MessageKeys.VALIDATION_FAILED),
				request.getRequestURI(), validationErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		logger.error("Unexpected error", ex);
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

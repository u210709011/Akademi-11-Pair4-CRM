package com.etiya.crm.partyservice.business.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.shared.contracts.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

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
				HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation failed", request.getRequestURI(), errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, BusinessException ex, HttpServletRequest request) {
		String message = resolve(ex.getMessageKey(), ex.getArgs());
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}

	private String resolve(String key, Object... args) {
		return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
	}
}

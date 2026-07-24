package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.shared.contracts.error.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAddressNotFound(AddressNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(AddressLimitExceededException.class)
	public ResponseEntity<ErrorResponse> handleAddressLimitExceeded(AddressLimitExceededException ex,
			HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(DuplicateNationalIdException.class)
	public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateNationalIdException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(InvalidBirthDateException.class)
	public ResponseEntity<ErrorResponse> handleInvalidBirthDate(InvalidBirthDateException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex, request);
	}

	@ExceptionHandler(BillingAccountAddressRequiredException.class)
	public ResponseEntity<ErrorResponse> handleBillingAccountAddressRequired(BillingAccountAddressRequiredException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex, request);
	}

	@ExceptionHandler(BillingAccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBillingAccountNotFound(BillingAccountNotFoundException ex,
			HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler({ PrimaryAddressCannotBeDeletedException.class, AddressLinkedToAccountException.class,
			BillingAccountActiveCannotBeDeletedException.class, CustomerHasActiveBillingAccountException.class })
	public ResponseEntity<ErrorResponse> handleGuardViolation(BusinessException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(IdentityVerificationFailedException.class)
	public ResponseEntity<ErrorResponse> handleIdentityVerificationFailed(IdentityVerificationFailedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex, request);
	}

	@ExceptionHandler(OnboardingFailedException.class)
	public ResponseEntity<ErrorResponse> handleOnboardingFailed(OnboardingFailedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_GATEWAY, ex, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(FieldError::getDefaultMessage)
				.orElseGet(() -> resolve(MessageKeys.FIELD_REQUIRED));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
						message, request.getRequestURI()));
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

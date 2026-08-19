package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.shared.contracts.error.AbstractDownstreamExceptionHandler;
import com.etiya.crm.shared.contracts.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.customerservice.constants.LogMessages;
import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
/** Customer servisindeki hataları ortak API formatına çevirir. */
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

	@Override
	protected String parameterTypeMismatchMessage(String parameterName) {
		return resolve(MessageKeys.PARAMETER_TYPE_MISMATCH, parameterName);
	}

	@Override
	protected String invalidRequestParameterMessage() {
		return resolve(MessageKeys.INVALID_REQUEST_PARAMETER);
	}

	@Override
	protected String missingParameterMessage(String parameterName) {
		return resolve(MessageKeys.MISSING_REQUEST_PARAMETER, parameterName);
	}

	@Override
	protected String methodNotSupportedMessage(String httpMethod) {
		return resolve(MessageKeys.METHOD_NOT_SUPPORTED, httpMethod);
	}

	@Override
	protected String routeNotFoundMessage() {
		return resolve(MessageKeys.ROUTE_NOT_FOUND);
	}

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

	@ExceptionHandler(SearchFilterRequiredException.class)
	public ResponseEntity<ErrorResponse> handleSearchFilterRequired(SearchFilterRequiredException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex, request);
	}

	@ExceptionHandler({ BillingAccountAddressRequiredException.class, BillingAccountAddressConflictException.class })
	public ResponseEntity<ErrorResponse> handleBillingAccountAddressRequired(BusinessException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex, request);
	}

	@ExceptionHandler(BillingAccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBillingAccountNotFound(BillingAccountNotFoundException ex,
			HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler({ PrimaryAddressCannotBeDeletedException.class, AddressLinkedToAccountException.class,
			BillingAccountActiveCannotBeDeletedException.class, CustomerHasActiveBillingAccountException.class,
			DefaultAccountCannotBeDeletedException.class, DefaultAccountCannotBeChangedException.class,
			BillingAccountHasActiveProductsException.class, AccountNumberCollisionException.class })
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

	/** Request parametrelerindeki bean validation hatalarını karşılar. */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		String message = ex.getConstraintViolations().stream()
				.findFirst()
				.map(violation -> violation.getMessage())
				.orElseGet(() -> resolve(MessageKeys.INVALID_REQUEST_PARAMETER));
		return build(HttpStatus.BAD_REQUEST, message, request);
	}

	// Ortak parametre ve downstream hataları üst sınıftan yönetilir.

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error(LogMessages.UNEXPECTED_ERROR, ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, resolve(MessageKeys.UNEXPECTED_ERROR), request);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, BusinessException ex, HttpServletRequest request) {
		return build(status, resolve(ex.getMessageKey(), ex.getArgs()), request);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}

	private String resolve(String key, Object... args) {
		return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
	}
}

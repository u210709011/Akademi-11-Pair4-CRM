package com.etiya.crm.shared.contracts.error;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;


/** Feign ve ortak HTTP hatalarını standart error response'a dönüştürür. */
public abstract class AbstractDownstreamExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AbstractDownstreamExceptionHandler.class);

	private static final int MAX_UNWRAP_DEPTH = 10;

	private static final String UNKNOWN_REQUEST_DESCRIPTION = "unknown";

	private final ObjectMapper objectMapper;

	protected AbstractDownstreamExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/** Downstream gövdesi okunamadığında kullanılacak mesajı sağlar. */
	protected abstract String downstreamCallFailedMessage();

	/** Circuit breaker açıkken kullanılacak mesajı sağlar. */
	protected abstract String downstreamUnavailableMessage();

	/** Tip uyuşmazlığı için yerelleştirilmiş mesajı sağlar. */
	protected abstract String parameterTypeMismatchMessage(String parameterName);

	/** Eksik parametre için yerelleştirilmiş mesajı sağlar. */
	protected abstract String missingParameterMessage(String parameterName);

	/** Geçersiz parametre için yerelleştirilmiş mesajı sağlar. */
	protected abstract String invalidRequestParameterMessage();

	/** Desteklenmeyen HTTP metodu için mesajı sağlar. */
	protected abstract String methodNotSupportedMessage(String httpMethod);

	/** Bulunamayan route için yerelleştirilmiş mesajı sağlar. */
	protected abstract String routeNotFoundMessage();

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, parameterTypeMismatchMessage(ex.getName()), request);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, missingParameterMessage(ex.getParameterName()), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, invalidRequestParameterMessage(), request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.METHOD_NOT_ALLOWED, methodNotSupportedMessage(ex.getMethod()), request);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, routeNotFoundMessage(), request);
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
		return buildFeignExceptionResponse(ex, request);
	}

	@ExceptionHandler(CallNotPermittedException.class)
	public ResponseEntity<ErrorResponse> handleCircuitBreakerOpen(CallNotPermittedException ex,
			HttpServletRequest request) {
		return buildCircuitBreakerOpenResponse(ex, request);
	}

	/** Fallback sarmalayıcısındaki asıl Feign veya circuit breaker hatasını bulur. */
	@ExceptionHandler(NoFallbackAvailableException.class)
	public ResponseEntity<ErrorResponse> handleNoFallbackAvailable(NoFallbackAvailableException ex,
			HttpServletRequest request) {
		Throwable knownCause = findKnownCause(ex.getCause());
		if (knownCause instanceof FeignException feignException) {
			return buildFeignExceptionResponse(feignException, request);
		}
		if (knownCause instanceof CallNotPermittedException circuitBreakerException) {
			return buildCircuitBreakerOpenResponse(circuitBreakerException, request);
		}
		log.error(LogMessages.NO_FALLBACK_UNEXPECTED_CAUSE, String.valueOf(ex.getCause()), ex);
		HttpStatus status = HttpStatus.BAD_GATEWAY;
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), status.getReasonPhrase(),
				downstreamCallFailedMessage(), request.getRequestURI()));
	}

	/** Hata zincirinde bilinen downstream nedenini arar. */
	private Throwable findKnownCause(Throwable cause) {
		Throwable current = cause;
		for (int depth = 0; current != null && depth < MAX_UNWRAP_DEPTH; depth++) {
			if (current instanceof FeignException || current instanceof CallNotPermittedException) {
				return current;
			}
			current = current.getCause();
		}
		return null;
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}

	private ResponseEntity<ErrorResponse> buildFeignExceptionResponse(FeignException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.status());
		if (status == null) {
			status = HttpStatus.BAD_GATEWAY;
		}
		String requestDescription = ex.request() == null ? UNKNOWN_REQUEST_DESCRIPTION
				: ex.request().httpMethod() + " " + ex.request().url();
		log.warn(LogMessages.DOWNSTREAM_CALL_FAILED_LOG, requestDescription, ex.status(), ex.getMessage());
		String message = extractDownstreamMessage(ex).orElseGet(this::downstreamCallFailedMessage);
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}

	private ResponseEntity<ErrorResponse> buildCircuitBreakerOpenResponse(CallNotPermittedException ex,
			HttpServletRequest request) {
		log.warn(LogMessages.DOWNSTREAM_CIRCUIT_OPEN_LOG, ex.getCausingCircuitBreakerName());
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), status.getReasonPhrase(),
				downstreamUnavailableMessage(), request.getRequestURI()));
	}

	private Optional<String> extractDownstreamMessage(FeignException ex) {
		try {
			ErrorResponse downstream = objectMapper.readValue(ex.contentUTF8(), ErrorResponse.class);
			return Optional.ofNullable(downstream.message());
		} catch (Exception parseError) {
			log.warn(LogMessages.DOWNSTREAM_ERROR_BODY_PARSE_FAILED, ex.contentUTF8());
			return Optional.empty();
		}
	}
}

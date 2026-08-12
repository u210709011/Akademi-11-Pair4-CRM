package com.etiya.crm.shared.contracts.error;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * customer-service/party-service/order-service, Feign cagrilarindan donen hatalari
 * yakalayip caller'a HEM statusu HEM downstream mesajini yansitan birebir ayni kodu
 * kopyalamisti; contact-info-service'te bu hic yoktu ve Feign'den donen her sey (404/409
 * dahil) caller'a ham 500 olarak sizardi. Bug fix'in bir serviste yapilip digerlerinde
 * unutulmasini (tam olarak boyle oldu) imkansiz kilmak icin buraya tasindi - yeni bir
 * servis Feign client ekledi'ginde bu handler'i extend ederek "bedava" alir.
 *
 * Circuit breaker acikken (OPEN) resilience4j cagriyi Feign'e hic ulastirmadan
 * CallNotPermittedException firlatir - bu FeignException degildir ve ayri yakalanmazsa
 * yine ayni sekilde caller'a ham 500 olarak sizar, o yuzden burada birlikte ele alindi.
 *
 * KRITIK (B-03): projede hicbir @FeignClient icin fallback/fallbackFactory tanimli degil.
 * Spring Cloud OpenFeign'in FeignCircuitBreakerInvocationHandler'i, fallback yokken
 * circuitBreaker.run(supplier) (TEK parametreli overload) cagirir - bunun ic implementasyonu
 * decoratedSupplier.get()'i try/catch'e alip YAKALADIGI HER exception'i (circuit acik/kapali
 * fark etmeksizin - yani sIradan bir 404/409 FeignException DAHIL) NoFallbackAvailableException
 * (message, cause) olarak yeniden firlatir. Yani bu handler'daki ilk iki metot (handleFeignException/
 * handleCircuitBreakerOpen) pratikte NEREDEYSE HICBIR ZAMAN dogrudan tetiklenmez - gercek exception
 * hep NoFallbackAvailableException'a sarilmis, .getCause()'da gelir. Bu SARMALAMAYI cozup asil
 * exception'in tipine gore ayni iki mantiga devretmezsek, downstream'in dogru donen HER 4xx/409/404
 * (FR-006..FR-011'deki butun guard'lar dahil) caller'a ham 500 olarak sizar.
 *
 * DUZELTME 2 (QA canli ortam doğrulamasi ile bulundu): ex.getCause() tek basina yetmiyor - bazi
 * cagri yollarinda (orn. Resilience4j'in ic Executor/Future tabanli calistirmasindan gecen
 * cagrilarda) sarmalama iki katli oluyor:
 *   NoFallbackAvailableException -> java.util.concurrent.ExecutionException -> feign.FeignException
 * Tek seviye getCause() bu durumda ExecutionException'da duruyor, FeignException'a hic ulasmiyor
 * ve generic 502 dalina duşuyordu (orn. PUT /customers/{id}/individual duplicate TC no -> 409
 * yerine 502). Asagidaki findKnownCause artik FeignException/CallNotPermittedException bulana
 * kadar (ya da MAX_UNWRAP_DEPTH sinirina kadar) TUM zinciri dolasir.
 */
public abstract class AbstractDownstreamExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AbstractDownstreamExceptionHandler.class);

	/** Cause zincirinde sonsuz donguye karsi savunma - gercek zincirler bundan cok daha kisa. */
	private static final int MAX_UNWRAP_DEPTH = 10;

	/** ex.request() null geldiginde (Feign istegi kuramadan basarisiz oldugunda) log'da yerini tutar. */
	private static final String UNKNOWN_REQUEST_DESCRIPTION = "unknown";

	private final ObjectMapper objectMapper;

	protected AbstractDownstreamExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/** Downstream govdesi coz(ul)emedi ya da bos ise kullanilacak yerellestirilmis fallback mesaji. */
	protected abstract String downstreamCallFailedMessage();

	/** Circuit breaker OPEN durumundayken kullanilacak yerellestirilmis fallback mesaji. */
	protected abstract String downstreamUnavailableMessage();

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
		return buildFeignExceptionResponse(ex, request);
	}

	@ExceptionHandler(CallNotPermittedException.class)
	public ResponseEntity<ErrorResponse> handleCircuitBreakerOpen(CallNotPermittedException ex,
			HttpServletRequest request) {
		return buildCircuitBreakerOpenResponse(ex, request);
	}

	/**
	 * bkz. sinif ustu "KRITIK (B-03)" notu: fallback tanimlanmadigi surece TUM Feign/circuit-breaker
	 * hatalari buraya NoFallbackAvailableException olarak duser, gercek hata .getCause()'dadir -
	 * onu coz(up) yukaridaki iki handler'in ayni mantigina devrederiz.
	 */
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

	/**
	 * bkz. sinif ustu "DUZELTME 2" notu: ExecutionException gibi ara sarmalayicilari atlayip
	 * zincirde FeignException/CallNotPermittedException arar - bulamazsa null doner.
	 */
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

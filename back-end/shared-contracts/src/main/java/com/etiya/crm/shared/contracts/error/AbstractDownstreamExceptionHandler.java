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
 *
 * B-15: customer-service'te B-09 (MethodArgumentTypeMismatchException) ve B-10
 * (IllegalArgumentException) icin buraya tasinmadan once yerel handler'lar eklenmisti;
 * order-service/product-service'te bu ikisi hic yoktu ve HttpRequestMethodNotSupportedException/
 * MissingServletRequestParameterException hicbir serviste yoktu - hepsi @ExceptionHandler(Exception.class)
 * dalina duşup yanlis metot/eksik parametre gonderen caller'a bile 500 donduruyordu. Ayni aile bir
 * kez daha (dorduncu bir serviste) tek tek yakalanmasin diye burada, subclass'larin sadece
 * yerellestirilmis mesaji sagladigi hook metotlarla merkezilestirildi.
 *
 * NoHandlerFoundException notu: DispatcherServlet bunu SADECE
 * spring.mvc.throw-exception-if-no-handler-found=true (+ spring.web.resources.add-mappings=false)
 * ayariyla firlatir; bu repo'da o ayar config-server'in ayri git deposunda yasiyor ve buradan
 * degistirilemiyor. Ayar acilana kadar bu handler hicbir zaman tetiklenmez - tamamen eslesmeyen
 * bir path zaten Spring Boot'un varsayilan /error akisi uzerinden (farkli govde formatiyla ama
 * dogru 404 statusuyle) doner, 500'e sizmaz. Ayar acildiginda handler hazir bekler.
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

	/** B-15: MethodArgumentTypeMismatchException icin (parametre adi ile) yerellestirilmis mesaj. */
	protected abstract String parameterTypeMismatchMessage(String parameterName);

	/** B-15: MissingServletRequestParameterException icin (parametre adi ile) yerellestirilmis mesaj. */
	protected abstract String missingParameterMessage(String parameterName);

	/** B-15: caller'in gecersiz bir deger gonderdigi genel IllegalArgumentException durumu icin mesaj. */
	protected abstract String invalidRequestParameterMessage();

	/** B-15: HttpRequestMethodNotSupportedException icin (kullanilan HTTP metodu ile) mesaj. */
	protected abstract String methodNotSupportedMessage(String httpMethod);

	/** B-15: NoHandlerFoundException icin mesaj (bkz. sinif ustu not - bugun icin genelde tetiklenmez). */
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

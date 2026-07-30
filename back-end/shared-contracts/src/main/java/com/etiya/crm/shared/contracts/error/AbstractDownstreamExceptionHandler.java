package com.etiya.crm.shared.contracts.error;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
public abstract class AbstractDownstreamExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AbstractDownstreamExceptionHandler.class);

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
		HttpStatus status = HttpStatus.resolve(ex.status());
		if (status == null) {
			status = HttpStatus.BAD_GATEWAY;
		}
		String message = extractDownstreamMessage(ex).orElseGet(this::downstreamCallFailedMessage);
		return ResponseEntity.status(status)
				.body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}

	@ExceptionHandler(CallNotPermittedException.class)
	public ResponseEntity<ErrorResponse> handleCircuitBreakerOpen(CallNotPermittedException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), status.getReasonPhrase(),
				downstreamUnavailableMessage(), request.getRequestURI()));
	}

	private Optional<String> extractDownstreamMessage(FeignException ex) {
		try {
			ErrorResponse downstream = objectMapper.readValue(ex.contentUTF8(), ErrorResponse.class);
			return Optional.ofNullable(downstream.message());
		} catch (Exception parseError) {
			log.warn("Downstream Feign hata govdesi coz(ul)emedi: {}", ex.contentUTF8());
			return Optional.empty();
		}
	}
}

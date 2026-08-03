package com.etiya.crm.shared.contracts.error;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tum servislerin @RestControllerAdvice/AuthExceptionHandler'inda kullandigi
 * ortak hata govdesi. Daha once api-gateway/customer-service birbirinden
 * bagimsiz ama birebir ayni sekli tasiyan yerel kopyalar tutuyordu;
 * contact-info-service farkli bir sekil (validationErrors var, error/path
 * yok), lookup-service/party-service ise hic tipli bir govde kullanmiyordu
 * (ham Map&lt;String,String&gt;). API tuketen taraf (front-end, Swagger) icin
 * tek bir kontrat olsun diye buraya tasindi.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standart hata govdesi.")
public record ErrorResponse(

		Instant timestamp,

		@Schema(example = "404")
		int status,

		@Schema(description = "HTTP status'un reason phrase'i.", example = "Not Found")
		String error,

		String message,

		@Schema(description = "Hatanin olustugu istek path'i.", example = "/api/v1/customers/999")
		String path,

		@Schema(description = "Validasyon hatasi ise alan adi -> hata mesaji eslemesi; aksi halde null.")
		Map<String, String> validationErrors) {

	public static ErrorResponse of(int status, String error, String message, String path) {
		return new ErrorResponse(Instant.now(), status, error, message, path, null);
	}

	public static ErrorResponse of(int status, String error, String message, String path,
			Map<String, String> validationErrors) {
		return new ErrorResponse(Instant.now(), status, error, message, path, validationErrors);
	}
}

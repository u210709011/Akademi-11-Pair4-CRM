package com.etiya.crm.contactinfoservice.business.exceptions;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.etiya.crm.shared.contracts.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@Mock
	private MessageSource messageSource;

	@Mock
	private HttpServletRequest request;

	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler(messageSource, objectMapper);
		when(request.getRequestURI()).thenReturn("/api/v1/addresses/1");
	}

	@Test
	void handleNotFound_returns404() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Bulunamadi");

		ResponseEntity<ErrorResponse> response = handler.handleNotFound(new AddressNotFoundException(1L), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().message()).isEqualTo("Bulunamadi");
	}

	@Test
	void handleInvalidFormat_returns400() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Format hatali");

		ResponseEntity<ErrorResponse> response = handler.handleInvalidFormat(
				new InvalidContactMediumFormatException("error.contact-medium.invalid-email-format"), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void handleGuardViolation_returns409() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Cakisma");

		ResponseEntity<ErrorResponse> response = handler.handleGuardViolation(new AddressLimitExceededException(),
				request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void handleFeignException_resolvesStatusAndParsesDownstreamMessage() throws Exception {
		ErrorResponse downstreamBody = ErrorResponse.of(404, "Not Found", "Adres bulunamadi",
				"/api/v1/addresses/999");
		byte[] body = objectMapper.writeValueAsBytes(downstreamBody);
		FeignException ex = feignExceptionWithBody(404, body);

		ResponseEntity<ErrorResponse> response = handler.handleFeignException(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().message()).isEqualTo("Adres bulunamadi");
	}

	@Test
	void handleFeignException_fallsBackToGenericMessage_whenBodyNotParseable() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Downstream cagri basarisiz");
		FeignException ex = feignExceptionWithBody(500, "not-json".getBytes(StandardCharsets.UTF_8));

		ResponseEntity<ErrorResponse> response = handler.handleFeignException(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().message()).isEqualTo("Downstream cagri basarisiz");
	}

	@Test
	void handleFeignException_fallsBackTo502_whenStatusUnresolvable() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Downstream cagri basarisiz");
		FeignException ex = feignExceptionWithBody(999, "not-json".getBytes(StandardCharsets.UTF_8));

		ResponseEntity<ErrorResponse> response = handler.handleFeignException(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
	}

	@Test
	void handleValidation_returns400WithFieldErrorsMap() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Dogrulama basarisiz");
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors())
				.thenReturn(List.of(new FieldError("obj", "streetName", "zorunlu alan")));
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
				new org.springframework.core.MethodParameter(this.getClass().getMethods()[0], -1), bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().validationErrors()).containsEntry("streetName", "zorunlu alan");
	}

	@Test
	void handleUnexpected_returns500WithResolvedMessage() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Beklenmeyen hata");

		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("boom"), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().message()).isEqualTo("Beklenmeyen hata");
	}

	private FeignException feignExceptionWithBody(int status, byte[] body) {
		Request feignRequest = Request.create(Request.HttpMethod.GET, "/api/v1/addresses/999", Map.of(), null,
				StandardCharsets.UTF_8, null);
		Response feignResponse = Response.builder().status(status).reason("Error").request(feignRequest)
				.headers(Map.of()).body(body).build();
		return FeignException.errorStatus("AddressClient#getById(Long)", feignResponse);
	}

}

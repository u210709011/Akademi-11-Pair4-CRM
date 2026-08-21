package com.etiya.crm.partyservice.business.exceptions;

import java.util.List;
import java.util.Locale;

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

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler(messageSource, new ObjectMapper());
		when(request.getRequestURI()).thenReturn("/api/v1/individuals/by-party-role/100");
	}

	@Test
	void handleDuplicateNationalId_returns409() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Zaten kayitli");

		ResponseEntity<ErrorResponse> response = handler.handleDuplicateNationalId(
				new DuplicateNationalIdException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody().message()).isEqualTo("Zaten kayitli");
	}

	@Test
	void handleNotFound_returns404_forAnyNotFoundException() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Bulunamadi");

		ResponseEntity<ErrorResponse> response = handler.handleNotFound(
				new PartyRoleNotFoundException(100L), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void handleValidationException_returns400WithFieldErrorsMap() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Dogrulama basarisiz");
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors())
				.thenReturn(List.of(new FieldError("obj", "firstName", "zorunlu alan")));
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
				new org.springframework.core.MethodParameter(this.getClass().getMethods()[0], -1), bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().validationErrors()).containsEntry("firstName", "zorunlu alan");
	}

	@Test
	void handleUnexpected_returns500WithResolvedMessage() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Beklenmeyen hata");

		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("boom"), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().message()).isEqualTo("Beklenmeyen hata");
	}
}

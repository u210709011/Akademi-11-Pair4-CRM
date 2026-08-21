package com.etiya.crm.customerservice.business.exceptions;

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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @RestControllerAdvice metotlarinin dogru HTTP status'e + cozumlenmis
 * mesaja map ettigini dogrular. MessageSource mock'lanir, gercek
 * messages.properties dosyalarina bagimlilik yok.
 */
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
		when(request.getRequestURI()).thenReturn("/api/v1/customers/10");
	}

	@Test
	void handleNotFound_returns404WithResolvedMessage() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Musteri bulunamadi");

		ResponseEntity<ErrorResponse> response = handler.handleNotFound(new CustomerNotFoundException(10L), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().message()).isEqualTo("Musteri bulunamadi");
		assertThat(response.getBody().path()).isEqualTo("/api/v1/customers/10");
	}

	@Test
	void handleAddressNotFound_returns404() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Adres bulunamadi");

		ResponseEntity<ErrorResponse> response = handler.handleAddressNotFound(
				new AddressNotFoundException(10L, 20L), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void handleAddressLimitExceeded_returns409() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Adres siniri asildi");

		ResponseEntity<ErrorResponse> response = handler.handleAddressLimitExceeded(
				new AddressLimitExceededException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void handleDuplicate_returns409() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Zaten kayitli");

		ResponseEntity<ErrorResponse> response = handler.handleDuplicate(
				new DuplicateNationalIdException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void handleInvalidBirthDate_returns400() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Gecersiz tarih");

		ResponseEntity<ErrorResponse> response = handler.handleInvalidBirthDate(
				new InvalidBirthDateException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void handleSearchFilterRequired_returns400() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Filtre gerekli");

		ResponseEntity<ErrorResponse> response = handler.handleSearchFilterRequired(
				new SearchFilterRequiredException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void handleBillingAccountAddressRequired_returns400() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Adres gerekli");

		ResponseEntity<ErrorResponse> response = handler.handleBillingAccountAddressRequired(
				new BillingAccountAddressRequiredException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void handleBillingAccountNotFound_returns404() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Fatura hesabi bulunamadi");

		ResponseEntity<ErrorResponse> response = handler.handleBillingAccountNotFound(
				new BillingAccountNotFoundException(10L, 30L), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void handleGuardViolation_returns409() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Islem engellendi");

		ResponseEntity<ErrorResponse> response = handler.handleGuardViolation(
				new PrimaryAddressCannotBeDeletedException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void handleIdentityVerificationFailed_returns422() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Kimlik dogrulanamadi");

		ResponseEntity<ErrorResponse> response = handler.handleIdentityVerificationFailed(
				new IdentityVerificationFailedException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void handleOnboardingFailed_returns502() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Onboarding basarisiz");

		ResponseEntity<ErrorResponse> response = handler.handleOnboardingFailed(
				new OnboardingFailedException(new RuntimeException("boom")), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
	}

	@Test
	void handleValidation_usesFirstFieldErrorMessage() {
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "firstName", "zorunlu alan")));
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
				new org.springframework.core.MethodParameter(this.getClass().getMethods()[0], -1), bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().message()).isEqualTo("zorunlu alan");
	}

	@Test
	void handleValidation_fallsBackToResolvedMessage_whenNoFieldErrors() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Alan zorunlu");
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(List.of());
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
				new org.springframework.core.MethodParameter(this.getClass().getMethods()[0], -1), bindingResult);

		ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

		assertThat(response.getBody().message()).isEqualTo("Alan zorunlu");
	}

	@Test
	void handleConstraintViolation_usesFirstViolationMessage() {
		ConstraintViolation<?> violation = mock(ConstraintViolation.class);
		when(violation.getMessage()).thenReturn("gecersiz deger");
		ConstraintViolationException ex = new ConstraintViolationException(java.util.Set.of(violation));

		ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().message()).isEqualTo("gecersiz deger");
	}

	@Test
	void handleUnexpected_returns500WithResolvedMessage() {
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Beklenmeyen hata");

		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("boom"), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().message()).isEqualTo("Beklenmeyen hata");
	}
}

package com.etiya.crm.customerservice.business.dtos.requests;

import java.util.List;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Musteri ekleme (onboarding) istegi. Tek istek uc servise yazar: party-service
 * (kisi + rol), customer-service (musteri + varsayilan 223 tipi hesap),
 * contact-info-service (adres + iletisim). ACC-001..025.
 *
 * customer/account bilgisi kullanicidan alinmaz: ACC-025 geregi musteri
 * olusturulurken otomatik olarak 223 tipinde tek bir hesap acilir. Bu
 * istekten once genellikle POST /onboarding/verify-identity ile ayni
 * individual bilgisi dogrulanir (UI akisinda ayri bir adim).
 */
@Schema(description = SwaggerText.ONBOARD_CUSTOMER_REQUEST_SCHEMA_DESCRIPTION)
public record OnboardCustomerRequest(

		@Valid
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		IndividualInfo individual,

		@Schema(description = SwaggerText.ONBOARD_CUSTOMER_REQUEST_ADDRESSES_DESCRIPTION)
		@Valid
		@NotEmpty(message = "{" + MessageKeys.ADDRESS_MIN_REQUIRED + "}")
		@Size(max = 5, message = "{" + MessageKeys.ADDRESS_MAX_EXCEEDED + "}")
		List<AddressInfo> addresses,

		@Valid
		@NotNull(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		ContactInfo contact) {
}

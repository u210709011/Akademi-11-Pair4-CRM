package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * contact-info-service'e (CNTC_MEDIUM) gidecek iletisim bilgisi. ACC-019/020/021/022.
 * Email ve mobilePhone zorunlu; homePhone ve fax opsiyoneldir. Servis katmani
 * dolu olan her alani ayri bir CNTC_MEDIUM satirina cevirir. Musteri basina
 * tek bir contact bilgisi vardir; hem onboarding'de hem edit'te (GET/PUT
 * /api/v1/customers/{custId}/contact) ayni sekil kullanilir.
 */
@Schema(description = "Musterinin iletisim bilgisi (musteri basina 1 adet).")
public record ContactInfo(

		@Schema(description = "E-posta - zorunlu", example = "ahmet.yilmaz@example.com")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Email(message = "{" + MessageKeys.EMAIL_INVALID + "}")
		String email,

		@Schema(description = "Cep telefonu - zorunlu, 10-15 haneli rakam", example = "5551234567", pattern = "^[0-9]{10,15}$")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String mobilePhone,

		@Schema(description = "Ev telefonu - opsiyonel, 10-15 haneli rakam", example = "3121234567", pattern = "^[0-9]{10,15}$")
		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String homePhone,

		@Schema(description = "Faks - opsiyonel, 10-15 haneli rakam", example = "null", pattern = "^[0-9]{10,15}$")
		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String fax) {
}

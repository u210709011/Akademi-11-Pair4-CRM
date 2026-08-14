package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;
import com.etiya.crm.customerservice.constants.SwaggerText;

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
@Schema(description = SwaggerText.CONTACT_INFO_SCHEMA_DESCRIPTION)
public record ContactInfo(

		@Schema(description = SwaggerText.CONTACT_INFO_EMAIL_DESCRIPTION, example = "ahmet.yilmaz@example.com")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Email(message = "{" + MessageKeys.EMAIL_INVALID + "}")
		String email,

		@Schema(description = SwaggerText.CONTACT_INFO_MOBILE_PHONE_DESCRIPTION, example = "5551234567", pattern = "^5[0-9]{9}$")
		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^5[0-9]{9}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String mobilePhone,

		@Schema(description = SwaggerText.CONTACT_INFO_HOME_PHONE_DESCRIPTION, example = "2121234567", pattern = "^2[0-9]{9}$")
		@Pattern(regexp = "^2[0-9]{9}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String homePhone,

		@Schema(description = SwaggerText.CONTACT_INFO_FAX_DESCRIPTION, pattern = "^[0-9]{10,11}$")
		@Pattern(regexp = "^[0-9]{10,11}$", message = "{" + MessageKeys.FAX_INVALID + "}")
		String fax) {
}

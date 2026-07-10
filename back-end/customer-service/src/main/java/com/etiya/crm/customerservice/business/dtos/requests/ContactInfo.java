package com.etiya.crm.customerservice.business.dtos.requests;

import com.etiya.crm.customerservice.constants.MessageKeys;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * contact-address-service'e (CNTC_MEDIUM) gidecek iletisim bilgisi. ACC-019/020/021/022.
 * Email ve mobilePhone zorunlu; homePhone ve fax opsiyoneldir. Servis katmani
 * dolu olan her alani ayri bir CNTC_MEDIUM satirina cevirir.
 */
public record ContactInfo(

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Email(message = "{" + MessageKeys.EMAIL_INVALID + "}")
		String email,

		@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String mobilePhone,

		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String homePhone,

		@Pattern(regexp = "^[0-9]{10,15}$", message = "{" + MessageKeys.PHONE_INVALID + "}")
		String fax) {
}

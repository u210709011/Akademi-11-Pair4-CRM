package com.etiya.crm.contactinfoservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger UI: http://localhost:8084/swagger-ui.html
 * OpenAPI JSON: http://localhost:8084/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

	private static final String BEARER_SCHEME = "bearerAuth";

	@Bean
	public OpenAPI contactInfoServiceOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Contact Info Service API")
						.description("""
								Adres (ADDR) ve iletisim (CNTC_MEDIUM) kayitlarinin CRUD'u. Her iki tablo da
								polimorfiktir: bir kayit rowId+dataTypeId ciftiyle "kime ait oldugunu" belirtir
								(bugun icin tek DATA_TYPE=CUST/102 - musteri). Bu servis rowId'nin bir customer,
								party ya da baska bir sey oldugunu bilmez/bilmemelidir; customer'a ozel kurallar
								(max 5 adres, tek primary secimi haric - o burada uygulanir) customer-service'te
								uygulanir.
								""")
						.version("v1"))
				.servers(List.of(
						new Server().url("http://localhost:8080").description("API Gateway uzerinden (onerilen)"),
						new Server().url("/").description("Bu servisin kendi adresi (gateway'i atlar)")))
				.components(new Components()
						.addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
	}
}

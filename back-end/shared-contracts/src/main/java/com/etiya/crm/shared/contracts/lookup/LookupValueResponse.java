package com.etiya.crm.shared.contracts.lookup;

import io.swagger.v3.oas.annotations.media.Schema;

/** lookup-service GET /api/v1/lookups/... yanit govdesi. */
@Schema(description = "Tek bir lookup degeri.")
public record LookupValueResponse(

		@Schema(description = "Deger id'si (VALUE_ID) - diger servislerde FK gibi referans alinir.", example = "201")
		Long id,

		@Schema(description = "Kod bazli erisim icin (opsiyonel, bazi gruplarda null olabilir - ornegin CITY).", example = "ACTIVE")
		String code,

		@Schema(description = "Insan tarafindan okunabilir deger.", example = "Aktif")
		String value) {
}

package com.etiya.crm.shared.contracts.lookup;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * lookup-service'in eski GET /api/v1/lookups/... yanit govdesi.
 *
 * @deprecated Bu endpoint lookup-service'te artik yok (GNL_TP/GNL_ST/TYPE_VALUE semasina gecildi).
 * Sadece party-service/customer-service/contact-info-service henuz yeni semaya gecmedigi icin
 * duruyor - o servisler guncellenince kaldirilmali.
 */
@Deprecated(forRemoval = true)
@Schema(description = "Tek bir lookup degeri.")
public record LookupValueResponse(

		@Schema(description = "Deger id'si (VALUE_ID) - diger servislerde FK gibi referans alinir.", example = "201")
		Long id,

		@Schema(description = "Kod bazli erisim icin (opsiyonel, bazi gruplarda null olabilir - ornegin CITY).", example = "ACTIVE")
		String code,

		@Schema(description = "Insan tarafindan okunabilir deger.", example = "Aktif")
		String value) {
}

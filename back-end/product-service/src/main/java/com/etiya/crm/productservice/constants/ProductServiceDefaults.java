package com.etiya.crm.productservice.constants;

/**
 * customer-service'teki AccountDefaults.formatAccountNo ile ayni kural: campaign/offering/product
 * icin "No" alani PREFIX metni tasimaz, sadece bu uzunlukta sifirla soldan doldurulmus bir sayidir
 * (ör. 42 -> "000042"). Prefix (CMP-/OFR-/PRD-) sadece frontend'de gorsel olarak eklenir.
 */
public final class ProductServiceDefaults {

	public static final int NO_LENGTH = 6;

	public static String formatNo(long id) {
		return String.format("%0" + NO_LENGTH + "d", id);
	}

	private ProductServiceDefaults() {
	}
}

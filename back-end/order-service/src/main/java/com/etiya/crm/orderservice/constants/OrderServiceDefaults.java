package com.etiya.crm.orderservice.constants;

/**
 * customer-service'teki AccountDefaults.formatAccountNo ile ayni kural (bkz. product-service
 * ProductServiceDefaults) - detail-customer'daki hesap urunleri tablosunda prodId'yi PRD- prefix'i
 * ile gostermek icin; product-service'e gitmeye gerek yok, tek yonlu deterministik bir bicimlendirme.
 */
public final class OrderServiceDefaults {

	public static final int NO_LENGTH = 6;

	public static String formatNo(long id) {
		return String.format("%0" + NO_LENGTH + "d", id);
	}

	private OrderServiceDefaults() {
	}
}
